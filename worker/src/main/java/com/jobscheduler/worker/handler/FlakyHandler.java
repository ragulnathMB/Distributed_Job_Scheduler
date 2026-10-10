package com.jobscheduler.worker.handler;

import com.jobscheduler.common.DispatchRequest;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Fails randomly. payload: {"failureRate": 0.5} */
@Component
public class FlakyHandler implements JobHandler {

    @Override
    public String type() { return "FLAKY"; }

    @Override
    public String execute(Map<String, Object> payload, DispatchRequest request) {
        double rate = JobHandler.num(payload, "failureRate", 0.5);
        if (ThreadLocalRandom.current().nextDouble() < rate) {
            throw new IllegalStateException("random failure (attempt " + request.attempt() + ")");
        }
        return "succeeded on attempt " + request.attempt();
    }
}
