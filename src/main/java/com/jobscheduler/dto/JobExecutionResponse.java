package com.jobscheduler.dto;

import com.jobscheduler.entity.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Outbound representation of a single JobExecution row — powers the
 * "Execution History" / "Job Logs" endpoints and dashboard.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class JobExecutionResponse {

    private Long id;
    private Long jobId;
    private String workerId;
    private ExecutionStatus status;
    private Integer attemptNumber;
    private Instant leasedAt;
    private Instant startedAt;
    private Instant completedAt;
    private Long durationMs;
    private String errorMessage;
    private String output;
    private Instant createdAt;
}
