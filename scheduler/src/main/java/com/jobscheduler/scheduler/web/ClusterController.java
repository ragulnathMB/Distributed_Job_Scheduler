package com.jobscheduler.scheduler.web;

import com.jobscheduler.scheduler.config.SchedulerProperties;
import com.jobscheduler.scheduler.model.LeaderInfo;
import com.jobscheduler.scheduler.model.WorkerNode;
import com.jobscheduler.scheduler.service.JobService;
import com.jobscheduler.scheduler.service.LeaderElectionService;
import com.jobscheduler.scheduler.service.WorkerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Monitoring endpoints. */
@RestController
@RequestMapping("/api/cluster")
public class ClusterController {

    private final LeaderElectionService election;
    private final WorkerService workers;
    private final JobService jobs;
    private final SchedulerProperties props;

    public ClusterController(LeaderElectionService election, WorkerService workers, JobService jobs,
                             SchedulerProperties props) {
        this.election = election;
        this.workers = workers;
        this.jobs = jobs;
        this.props = props;
    }

    @GetMapping("/leader")
    public LeaderInfo leader() {
        return election.currentLeader();
    }

    @GetMapping("/workers")
    public List<WorkerNode> workers() {
        return workers.all();
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("thisNode", props.getNodeId());
        m.put("thisNodeIsLeader", election.isLeader());
        m.put("leader", election.currentLeader());
        m.put("workers", workers.all());
        m.put("jobsByStatus", jobs.stats());
        return m;
    }
}
