package com.jobscheduler.worker.handler;

import com.jobscheduler.common.DispatchRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Always fails. Use it to watch retries / exponential backoff / dead-letter. payload: {"message": "boom"} */
@Component
public class FailHandler implements JobHandler {

    @Override
    public String type() { return "FAIL"; }

    @Override
    public String execute(Map<String, Object> payload, DispatchRequest request) {
        throw new IllegalStateException(JobHandler.str(payload, "message", "intentional failure")
                + " (attempt " + request.attempt() + ")");
    }
}
