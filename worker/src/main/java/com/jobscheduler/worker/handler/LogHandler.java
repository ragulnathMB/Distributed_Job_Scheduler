package com.jobscheduler.worker.handler;

import com.jobscheduler.common.DispatchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/** payload: {"message": "hello"} */
@Component
public class LogHandler implements JobHandler {
    private static final Logger log = LoggerFactory.getLogger(LogHandler.class);

    @Override
    public String type() { return "LOG"; }

    @Override
    public String execute(Map<String, Object> payload, DispatchRequest request) {
        String message = JobHandler.str(payload, "message", "(no message)");
        log.info("[LOG job '{}'] {}", request.jobName(), message);
        return "logged: " + message;
    }
}
