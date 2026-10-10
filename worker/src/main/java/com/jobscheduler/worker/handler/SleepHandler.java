package com.jobscheduler.worker.handler;

import com.jobscheduler.common.DispatchRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Simulates a long-running task. payload: {"seconds": 5} */
@Component
public class SleepHandler implements JobHandler {

    @Override
    public String type() { return "SLEEP"; }

    @Override
    public String execute(Map<String, Object> payload, DispatchRequest request) throws Exception {
        double seconds = JobHandler.num(payload, "seconds", 2);
        Thread.sleep((long) (seconds * 1000));
        return "slept " + seconds + "s";
    }
}
