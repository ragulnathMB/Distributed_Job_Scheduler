package com.jobscheduler.dispatcher;

import com.jobscheduler.entity.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Executes the actual task handler logic for a Job on the workerExecutor pool,
 * wrapped with a hard timeout via CompletableFuture.orTimeout.
 *
 * In this educational implementation, "workers" run in-process as thread-pool
 * tasks rather than as separate physical worker nodes reachable over the
 * network — this keeps the concurrency model teachable without requiring
 * a message broker. WorkerRegistrationService + HeartbeatService still model
 * the *distributed* worker-node bookkeeping so the concepts transfer directly
 * to a real multi-node deployment (swap this class's internals for an RPC call).
 */
@Slf4j
@Component
public class TaskExecutor {

    private final Executor workerExecutor;

    public TaskExecutor(Executor workerExecutor) {
        this.workerExecutor = workerExecutor;
    }

    public record TaskResult(boolean success, String output, String errorMessage, String stackTrace) {
        static TaskResult ok(String output) {
            return new TaskResult(true, output, null, null);
        }

        static TaskResult failure(String message, String stackTrace) {
            return new TaskResult(false, null, message, stackTrace);
        }
    }

    /**
     * Runs the given job's task handler with a timeout. The actual handler
     * resolution (mapping taskHandler string -> executable logic) is
     * intentionally left as a simple registry lookup here — a real system
     * might resolve this to a Spring bean, a script, or an RPC target.
     */
    public CompletableFuture<TaskResult> execute(Job job, Callable<String> taskHandlerLogic) {
        CompletableFuture<TaskResult> future = CompletableFuture.supplyAsync(() -> {
            try {
                String output = taskHandlerLogic.call();
                return TaskResult.ok(output);
            } catch (Exception ex) {
                log.error("Job {} execution failed: {}", job.getId(), ex.getMessage());
                return TaskResult.failure(ex.getMessage(), stackTraceOf(ex));
            }
        }, workerExecutor);

        return future
                .orTimeout(job.getTimeoutSeconds(), TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof TimeoutException || ex instanceof TimeoutException) {
                        log.warn("Job {} timed out after {}s", job.getId(), job.getTimeoutSeconds());
                        return TaskResult.failure("Execution timed out", null);
                    }
                    return TaskResult.failure(ex.getMessage(), stackTraceOf(ex));
                });
    }

    private String stackTraceOf(Throwable t) {
        java.io.StringWriter sw = new java.io.StringWriter();
        t.printStackTrace(new java.io.PrintWriter(sw));
        return sw.toString();
    }
}
