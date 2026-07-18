package com.jobscheduler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.lang.reflect.Method;

/**
 * @Async methods returning void (none currently, but future-proofing) or
 * whose CompletableFuture chain throws before a handler is attached would
 * otherwise silently swallow exceptions. This ensures they're at least logged.
 */
@Slf4j
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object... params) ->
                log.error("Uncaught async exception in {}: {}", method.getName(), ex.getMessage(), ex);
    }
}
