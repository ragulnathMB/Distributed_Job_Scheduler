package com.jobscheduler.service;

import com.jobscheduler.dto.JobExecutionResponse;
import com.jobscheduler.dto.JobRequest;
import com.jobscheduler.dto.JobResponse;
import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobStatus;
import com.jobscheduler.exception.JobNotFoundException;
import com.jobscheduler.mapper.JobMapper;
import com.jobscheduler.repository.JobExecutionRepository;
import com.jobscheduler.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application-level CRUD + lifecycle operations on Job (creation, cancel,
 * pause, resume, history lookup). Deliberately separate from
 * SchedulerService, which owns the *distributed coordination* concerns
 * (leader election, claiming, recovery) — this keeps a clean boundary
 * between "managing jobs as a resource" and "scheduling jobs across nodes".
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobExecutionRepository executionRepository;
    private final JobMapper jobMapper;

    @Transactional
    public JobResponse createJob(JobRequest request) {
        Job job = jobMapper.toEntity(request);
        Job saved = jobRepository.save(job);
        log.info("Created job {} (type={}, nextRunAt={})", saved.getId(), saved.getJobType(), saved.getNextRunAt());
        return jobMapper.toResponse(saved);
    }

    public JobResponse getJob(Long id) {
        Job job = findOrThrow(id);
        return jobMapper.toResponse(job);
    }

    public List<JobResponse> listJobs(JobStatus status) {
        List<Job> jobs = status != null ? jobRepository.findByStatus(status) : jobRepository.findAll();
        return jobs.stream().map(jobMapper::toResponse).toList();
    }

    @Transactional
    public JobResponse cancelJob(Long id) {
        Job job = findOrThrow(id);
        job.setStatus(JobStatus.CANCELLED);
        job.setNextRunAt(null);
        jobRepository.save(job);
        log.info("Job {} cancelled", id);
        return jobMapper.toResponse(job);
    }

    @Transactional
    public JobResponse pauseJob(Long id) {
        Job job = findOrThrow(id);
        if (job.getStatus() != JobStatus.PENDING) {
            log.warn("Pausing job {} while in status {} (expected PENDING)", id, job.getStatus());
        }
        job.setStatus(JobStatus.PAUSED);
        jobRepository.save(job);
        return jobMapper.toResponse(job);
    }

    @Transactional
    public JobResponse resumeJob(Long id) {
        Job job = findOrThrow(id);
        if (job.getStatus() != JobStatus.PAUSED) {
            throw new IllegalStateException("Cannot resume a job that is not paused, id=" + id);
        }
        job.setStatus(JobStatus.PENDING);
        jobRepository.save(job);
        log.info("Job {} resumed", id);
        return jobMapper.toResponse(job);
    }

    public List<JobExecutionResponse> getExecutionHistory(Long jobId) {
        findOrThrow(jobId); // ensures 404 if job doesn't exist
        return executionRepository.findByJobIdOrderByCreatedAtDesc(jobId).stream()
                .map(jobMapper::toExecutionResponse)
                .toList();
    }

    private Job findOrThrow(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + id));
    }
}
