package com.jobscheduler.scheduler.web;

import com.jobscheduler.common.ExecutionResult;
import com.jobscheduler.common.WorkerRegistration;
import com.jobscheduler.scheduler.service.JobService;
import com.jobscheduler.scheduler.service.WorkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Worker-facing endpoints. Any scheduler node can serve them (all state lives in PostgreSQL),
 * so workers can fail over between scheduler nodes freely.
 */
@RestController
@RequestMapping("/api/internal")
public class InternalController {

    private final WorkerService workers;
    private final JobService jobs;

    public InternalController(WorkerService workers, JobService jobs) {
        this.workers = workers;
        this.jobs = jobs;
    }

    @PostMapping("/workers/register")
    public ResponseEntity<Void> register(@RequestBody WorkerRegistration registration) {
        workers.register(registration);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/workers/{workerId}/heartbeat")
    public ResponseEntity<Void> heartbeat(@PathVariable String workerId) {
        return workers.heartbeat(workerId) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/executions/{executionId}/result")
    public ResponseEntity<Void> result(@PathVariable long executionId, @RequestBody ExecutionResult result) {
        jobs.completeExecution(executionId, result);
        return ResponseEntity.ok().build();
    }
}
