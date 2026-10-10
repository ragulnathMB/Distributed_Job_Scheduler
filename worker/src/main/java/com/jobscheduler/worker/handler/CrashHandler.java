package com.jobscheduler.worker.handler;

import com.jobscheduler.common.DispatchRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * DEMO ONLY: hard-kills the worker JVM mid-job (no result is ever reported) so you can watch crash recovery:
 * the worker goes silent, is marked DEAD, and the job is reset to PENDING and picked up by another worker.
 */
@Component
public class CrashHandler implements JobHandler {

    @Override
    public String type() { return "CRASH"; }

    @Override
    public String execute(Map<String, Object> payload, DispatchRequest request) throws Exception {
        Thread.sleep(1500);
        Runtime.getRuntime().halt(137);
        return "unreachable";
    }
}
