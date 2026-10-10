package com.jobscheduler.scheduler.service;

import com.jobscheduler.common.WorkerRegistration;
import com.jobscheduler.scheduler.model.WorkerNode;
import com.jobscheduler.scheduler.repository.WorkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerService {
    private static final Logger log = LoggerFactory.getLogger(WorkerService.class);

    private final WorkerRepository workers;

    public WorkerService(WorkerRepository workers) {
        this.workers = workers;
    }

    public void register(WorkerRegistration r) {
        workers.upsert(r);
        log.info("Worker registered: {} at {}:{} (capacity {})", r.workerId(), r.host(), r.port(), r.capacity());
    }

    public boolean heartbeat(String workerId) {
        return workers.heartbeat(workerId);
    }

    public List<WorkerNode> all() {
        return workers.findAll();
    }
}
