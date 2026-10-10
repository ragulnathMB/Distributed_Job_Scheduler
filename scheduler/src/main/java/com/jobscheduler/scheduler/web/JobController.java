package com.jobscheduler.scheduler.web;

import com.jobscheduler.scheduler.model.Job;
import com.jobscheduler.scheduler.model.JobExecution;
import com.jobscheduler.scheduler.model.JobStatus;
import com.jobscheduler.scheduler.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService service;

    public JobController(JobService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Job> create(@Valid @RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public List<Job> list(@RequestParam(required = false) JobStatus status,
                          @RequestParam(defaultValue = "50") int limit,
                          @RequestParam(defaultValue = "0") int offset) {
        return service.list(status, limit, offset);
    }

    @GetMapping("/{id}")
    public Job get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Job update(@PathVariable UUID id, @Valid @RequestBody JobRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public Job cancel(@PathVariable UUID id) {
        return service.cancel(id);
    }

    @PostMapping("/{id}/pause")
    public Job pause(@PathVariable UUID id) {
        return service.pause(id);
    }

    @PostMapping("/{id}/resume")
    public Job resume(@PathVariable UUID id) {
        return service.resume(id);
    }

    @GetMapping("/{id}/executions")
    public List<JobExecution> executions(@PathVariable UUID id) {
        return service.executions(id);
    }
}
