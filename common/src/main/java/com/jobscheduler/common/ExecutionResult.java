package com.jobscheduler.common;

/** Sent by a worker back to any scheduler node when an attempt finishes. */
public record ExecutionResult(boolean success, String output, String error, String workerId) {
}
