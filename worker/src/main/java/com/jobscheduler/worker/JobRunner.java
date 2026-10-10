package com.jobscheduler.worker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobscheduler.common.DispatchRequest;
import com.jobscheduler.common.ExecutionResult;
import com.jobscheduler.worker.handler.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/** Runs dispatched jobs on a bounded pool, enforces the per-job timeout and reports the outcome. */
@Service
public class JobRunner {
    private static final Logger log = LoggerFactory.getLogger(JobRunner.class);

    private final Map<String, JobHandler> handlers = new HashMap<>();
    private final ObjectMapper mapper;
    private final SchedulerClient client;
    private final String workerId;
    private final int capacity;
    private final ExecutorService runnerPool;
    private final ExecutorService handlerPool = Executors.newCachedThreadPool();
    private final AtomicInteger active = new AtomicInteger();

    public JobRunner(List<JobHandler> handlerList, ObjectMapper mapper, SchedulerClient client,
                     @Value("${worker.id}") String workerId,
                     @Value("${worker.capacity}") int capacity) {
        handlerList.forEach(h -> handlers.put(h.type().toUpperCase(), h));
        this.mapper = mapper;
        this.client = client;
        this.workerId = workerId;
        this.capacity = capacity;
        this.runnerPool = Executors.newFixedThreadPool(capacity);
        log.info("Worker {} ready, capacity {}, handlers {}", workerId, capacity, handlers.keySet());
    }

    /** Returns false if the worker is already at capacity. */
    public boolean submit(DispatchRequest request) {
        if (active.incrementAndGet() > capacity) {
            active.decrementAndGet();
            return false;
        }
        runnerPool.submit(() -> {
            try {
                run(request);
            } finally {
                active.decrementAndGet();
            }
        });
        return true;
    }

    private void run(DispatchRequest req) {
        log.info("Running job '{}' ({}) attempt {} [{}]", req.jobName(), req.jobId(), req.attempt(), req.handlerType());
        ExecutionResult result;
        Future<String> future = null;
        try {
            JobHandler handler = handlers.get(req.handlerType().toUpperCase());
            if (handler == null) {
                throw new IllegalArgumentException("No handler registered for type '" + req.handlerType() + "'");
            }
            Map<String, Object> payload = parsePayload(req.payload());
            future = handlerPool.submit(() -> handler.execute(payload, req));
            String output = future.get(req.timeoutSeconds(), TimeUnit.SECONDS);
            result = new ExecutionResult(true, output, null, workerId);
        } catch (TimeoutException e) {
            future.cancel(true);
            result = failure("Timed out after " + req.timeoutSeconds() + "s");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            result = failure(cause.toString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result = failure("Interrupted");
        } catch (Exception e) {
            result = failure(e.toString());
        }
        report(req, result);
    }

    private ExecutionResult failure(String error) {
        return new ExecutionResult(false, null, error, workerId);
    }

    private Map<String, Object> parsePayload(String payload) throws Exception {
        if (payload == null || payload.isBlank()) {
            return new HashMap<>();
        }
        try {
            return mapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            Map<String, Object> m = new HashMap<>();   // plain-text payload
            m.put("message", payload);
            return m;
        }
    }

    /** Reports the result, retrying for a while: any scheduler node accepts it. */
    private void report(DispatchRequest req, ExecutionResult result) {
        String path = "/api/internal/executions/" + req.executionId() + "/result";
        for (int attempt = 1; attempt <= 10; attempt++) {
            int status = client.post(path, result);
            if (status >= 200 && status < 500) {
                log.info("Reported {} for job '{}' (execution {})", result.success() ? "SUCCESS" : "FAILURE",
                        req.jobName(), req.executionId());
                return;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        log.error("Could not report result of execution {}; lease expiry will recover the job", req.executionId());
    }
}
