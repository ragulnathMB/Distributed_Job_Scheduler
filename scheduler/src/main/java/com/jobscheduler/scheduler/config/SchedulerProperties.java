package com.jobscheduler.scheduler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scheduler")
public class SchedulerProperties {
    private String nodeId = "scheduler-local";
    private int leaderLeaseSeconds = 10;
    private long pollIntervalMs = 1000;
    private int jobLeaseGraceSeconds = 15;
    private int workerDeadAfterSeconds = 15;
    private int dispatchBatchSize = 50;
    private int maxBackoffSeconds = 300;

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public int getLeaderLeaseSeconds() { return leaderLeaseSeconds; }
    public void setLeaderLeaseSeconds(int v) { this.leaderLeaseSeconds = v; }
    public long getPollIntervalMs() { return pollIntervalMs; }
    public void setPollIntervalMs(long v) { this.pollIntervalMs = v; }
    public int getJobLeaseGraceSeconds() { return jobLeaseGraceSeconds; }
    public void setJobLeaseGraceSeconds(int v) { this.jobLeaseGraceSeconds = v; }
    public int getWorkerDeadAfterSeconds() { return workerDeadAfterSeconds; }
    public void setWorkerDeadAfterSeconds(int v) { this.workerDeadAfterSeconds = v; }
    public int getDispatchBatchSize() { return dispatchBatchSize; }
    public void setDispatchBatchSize(int v) { this.dispatchBatchSize = v; }
    public int getMaxBackoffSeconds() { return maxBackoffSeconds; }
    public void setMaxBackoffSeconds(int v) { this.maxBackoffSeconds = v; }
}
