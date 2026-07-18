package com.jobscheduler.controller;

import com.jobscheduler.entity.Worker;
import com.jobscheduler.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only visibility into the worker registry — powers the "Monitoring
 * Dashboard" requirement's cluster/worker health view. No mutation endpoints
 * are exposed here deliberately: worker lifecycle (register/heartbeat/leader
 * election) is internal, driven by WorkerRegistrationService, HeartbeatService,
 * and SchedulerService — not by external API calls.
 */
@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerRepository workerRepository;

    @GetMapping
    public List<Worker> listWorkers() {
        return workerRepository.findAll();
    }

    @GetMapping("/leader")
    public Worker getLeader() {
        return workerRepository.findByIsLeaderTrue().orElse(null);
    }
}
