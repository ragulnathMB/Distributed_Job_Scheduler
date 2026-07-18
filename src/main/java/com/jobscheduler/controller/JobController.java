package com.jobscheduler.controller;

import com.jobscheduler.dto.JobExecutionResponse;
import com.jobscheduler.dto.JobRequest;
import com.jobscheduler.dto.JobResponse;
import com.jobscheduler.entity.JobStatus;
import com.jobscheduler.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST surface for job management: creation, inspection, lifecycle
 * transitions, and execution history — the "REST API" and "Monitoring
 * Dashboard" backing endpoints from the functional requirements.
 */
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request) {
        JobResponse response = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJob(id));
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> listJobs(
            @RequestParam(required = false) JobStatus status) {
        return ResponseEntity.ok(jobService.listJobs(status));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<JobResponse> cancelJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.cancelJob(id));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<JobResponse> pauseJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.pauseJob(id));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<JobResponse> resumeJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.resumeJob(id));
    }

    @GetMapping("/{id}/executions")
    public ResponseEntity<List<JobExecutionResponse>> getExecutionHistory(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getExecutionHistory(id));
    }
}
