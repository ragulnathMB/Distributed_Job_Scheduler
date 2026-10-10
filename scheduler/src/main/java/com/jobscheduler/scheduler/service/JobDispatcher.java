package com.jobscheduler.scheduler.service;

import com.jobscheduler.common.DispatchRequest;
import com.jobscheduler.scheduler.config.SchedulerProperties;
import com.jobscheduler.scheduler.model.Job;
import com.jobscheduler.scheduler.model.WorkerNode;
import com.jobscheduler.scheduler.repository.JobExecutionRepository;
import com.jobscheduler.scheduler.repository.JobRepository;
import com.jobscheduler.scheduler.repository.WorkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Picks due jobs in priority order, claims each one optimistically, and pushes it to the least-loaded ALIVE worker.
 */
@Service
public class JobDispatcher {
    private static final Logger log = LoggerFactory.getLogger(JobDispatcher.class);

    private final JobRepository jobs;
    private final JobExecutionRepository executions;
    private final WorkerRepository workers;
    private final SchedulerProperties props;
    private final RestTemplate rest;
    private final TransactionTemplate tx;

    public JobDispatcher(JobRepository jobs, JobExecutionRepository executions, WorkerRepository workers,
                         SchedulerProperties props, RestTemplate rest, TransactionTemplate tx) {
        this.jobs = jobs;
        this.executions = executions;
        this.workers = workers;
        this.props = props;
        this.rest = rest;
        this.tx = tx;
    }

    public int dispatchDue() {
        Map<String, Integer> free = new HashMap<>();
        Map<String, WorkerNode> byId = new HashMap<>();
        int totalFree = 0;
        for (WorkerNode w : workers.findAlive()) {
            int f = w.capacity() - w.activeJobs();
            if (f > 0) {
                free.put(w.id(), f);
                byId.put(w.id(), w);
                totalFree += f;
            }
        }
        if (totalFree == 0) {
            return 0;
        }

        List<Job> candidates = jobs.findDueCandidates(Math.min(totalFree, props.getDispatchBatchSize()));
        int dispatched = 0;

        for (Job job : candidates) {
            String workerId = leastLoaded(free);
            if (workerId == null) {
                break;
            }
            WorkerNode worker = byId.get(workerId);
            int lease = job.timeoutSeconds() + props.getJobLeaseGraceSeconds();

            // Claim + open the execution record atomically. A lost claim (another node won) returns null.
            Long executionId = tx.execute(status -> {
                if (!jobs.tryClaim(job.id(), props.getNodeId(), workerId, lease)) {
                    return null;
                }
                long id = executions.create(job.id(), job.retryCount() + 1, workerId);
                jobs.setCurrentExecution(job.id(), id);
                return id;
            });
            if (executionId == null) {
                continue;
            }
            free.merge(workerId, -1, Integer::sum);

            DispatchRequest request = new DispatchRequest(executionId, job.id().toString(), job.name(),
                    job.handlerType(), job.payload(), job.retryCount() + 1, job.timeoutSeconds());
            try {
                rest.postForEntity("http://" + worker.host() + ":" + worker.port() + "/internal/execute",
                        request, Void.class);
                dispatched++;
                log.info("Dispatched job {} '{}' (prio {}, attempt {}) -> {}",
                        job.id(), job.name(), job.priority(), request.attempt(), workerId);
            } catch (Exception e) {
                String msg = "Dispatch to " + workerId + " failed: " + e.getMessage();
                log.warn(msg);
                free.remove(workerId);   // don't hammer an unreachable / full worker in this tick
                final long execId = executionId;
                tx.executeWithoutResult(status -> {
                    executions.finish(execId, "LOST", null, msg);
                    jobs.releaseAfterDispatchFailure(job.id(), execId, msg);
                });
            }
        }
        return dispatched;
    }

    private static String leastLoaded(Map<String, Integer> free) {
        String best = null;
        int bestFree = 0;
        for (Map.Entry<String, Integer> e : free.entrySet()) {
            if (e.getValue() > bestFree) {
                best = e.getKey();
                bestFree = e.getValue();
            }
        }
        return best;
    }
}
