package com.jobscheduler.scheduler.model;

import java.time.Instant;

public record WorkerNode(
        String id,
        String host,
        int port,
        int capacity,
        String status,
        Instant lastHeartbeat,
        Instant registeredAt,
        int activeJobs) {
}
