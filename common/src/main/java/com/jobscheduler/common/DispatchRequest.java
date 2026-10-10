package com.jobscheduler.common;

/** Sent by the leader scheduler to a worker to run one attempt of a job. */
public record DispatchRequest(
        long executionId,
        String jobId,
        String jobName,
        String handlerType,
        String payload,
        int attempt,
        int timeoutSeconds) {
}
