package com.jobscheduler.config;

import com.jobscheduler.worker.WorkerRegistrationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Runs one-time startup wiring: registers this node in the `workers` table
 * as soon as the application context is ready, so HeartbeatService and
 * leader election have a row to operate on from the very first scheduled tick.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SchedulerConfig {

    private final WorkerRegistrationService workerRegistrationService;

    @PostConstruct
    public void init() {
        log.info("Registering this node in the worker registry on startup");
        workerRegistrationService.registerSelf();
    }
}
