package com.jobscheduler.common;

/** Sent by a worker to register itself (idempotent upsert). */
public record WorkerRegistration(String workerId, String host, int port, int capacity) {
}
