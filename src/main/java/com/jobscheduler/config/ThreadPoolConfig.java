package com.jobscheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Two distinct pools, deliberately separated:
 *
 *  - dispatcherExecutor: small pool that only does the *lightweight* work of
 *    claiming a job (DB update) and handing it to a worker thread. Never runs
 *    user task code, so it can't be starved by a slow/hanging job.
 *
 *  - workerExecutor: larger pool that actually executes task handler logic.
 *    Isolating this means a flood of slow jobs saturates only this pool,
 *    not the dispatch loop — the scheduler keeps claiming/scheduling even
 *    under worker back-pressure. This is the concrete implementation of the
 *    "Backpressure" concept from Phase 9.
 *
 * CallerRunsPolicy is used as the rejection policy: when both pool and queue
 * are full, the calling thread (the poller) executes the task itself, which
 * naturally slows down job intake instead of throwing away work or OOMing.
 */
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "dispatcherExecutor")
    public ThreadPoolTaskExecutor dispatcherExecutor(
            @Value("${thread-pool.dispatcher-core-size}") int coreSize,
            @Value("${thread-pool.dispatcher-max-size}") int maxSize,
            @Value("${thread-pool.dispatcher-queue-capacity}") int queueCapacity) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("dispatcher-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean(name = "workerExecutor")
    public ThreadPoolTaskExecutor workerExecutor(
            @Value("${thread-pool.worker-core-size}") int coreSize,
            @Value("${thread-pool.worker-max-size}") int maxSize,
            @Value("${thread-pool.worker-queue-capacity}") int queueCapacity) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("worker-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
