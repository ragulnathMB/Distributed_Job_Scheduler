package com.jobscheduler.scheduler.model;

import java.time.Instant;

public record LeaderInfo(String nodeId, long term, Instant leaseExpiresAt, boolean leaseActive) {
}
