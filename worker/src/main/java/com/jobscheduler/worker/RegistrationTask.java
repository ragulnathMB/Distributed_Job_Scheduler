package com.jobscheduler.worker;

import com.jobscheduler.common.WorkerRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Registers this worker with the scheduler cluster and then sends heartbeats. */
@Component
public class RegistrationTask {
    private static final Logger log = LoggerFactory.getLogger(RegistrationTask.class);

    private final SchedulerClient client;
    private final WorkerRegistration registration;
    private volatile boolean registered = false;

    public RegistrationTask(SchedulerClient client,
                            @Value("${worker.id}") String id,
                            @Value("${worker.host}") String host,
                            @Value("${server.port}") int port,
                            @Value("${worker.capacity}") int capacity) {
        this.client = client;
        this.registration = new WorkerRegistration(id, host, port, capacity);
    }

    @Scheduled(initialDelay = 1000, fixedDelayString = "${worker.heartbeat-interval-ms:3000}")
    public void tick() {
        if (!registered) {
            int status = client.post("/api/internal/workers/register", registration);
            registered = status >= 200 && status < 300;
            if (registered) {
                log.info("Registered with scheduler as {} ({}:{})", registration.workerId(),
                        registration.host(), registration.port());
            }
            return;
        }
        int status = client.post("/api/internal/workers/" + registration.workerId() + "/heartbeat", null);
        if (status < 200 || status >= 300) {
            log.warn("Heartbeat failed (status {}), will re-register", status);
            registered = false;
        }
    }
}
