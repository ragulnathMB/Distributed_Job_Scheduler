package com.jobscheduler.worker;

import com.jobscheduler.common.DispatchRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExecuteController {

    private final JobRunner runner;

    public ExecuteController(JobRunner runner) {
        this.runner = runner;
    }

    /** Called by the leader scheduler. Returns immediately; the result is reported asynchronously. */
    @PostMapping("/internal/execute")
    public ResponseEntity<Void> execute(@RequestBody DispatchRequest request) {
        return runner.submit(request)
                ? ResponseEntity.status(HttpStatus.ACCEPTED).build()
                : ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
