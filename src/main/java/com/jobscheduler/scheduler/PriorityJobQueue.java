package com.jobscheduler.scheduler;

import com.jobscheduler.entity.Job;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In-memory priority queue used *within a single poll cycle* to order jobs
 * fetched from the DB before handing them to the dispatcher's thread pool.
 *
 * Why have this at all if the SQL query already ORDER BY priority DESC?
 * - The SQL ordering guarantees order *within one fetched page*. This queue
 *   is a teaching vehicle for how in-memory priority scheduling works, and
 *   gives us a single place to re-prioritize jobs if, e.g., a higher-priority
 *   job is pushed in in-process between poll cycles (see SchedulerService).
 * - It's thread-safe via an explicit lock since JobDispatcher's async workers
 *   pull from it concurrently.
 *
 * Complexity: offer/poll are O(log n); peek is O(1).
 */
@Component
public class PriorityJobQueue {

    private final PriorityQueue<Job> queue = new PriorityQueue<>(
            Comparator.comparing(Job::getPriority).reversed()
                    .thenComparing(Job::getNextRunAt)
    );

    private final ReentrantLock lock = new ReentrantLock();

    public void offer(Job job) {
        lock.lock();
        try {
            queue.offer(job);
        } finally {
            lock.unlock();
        }
    }

    public Job poll() {
        lock.lock();
        try {
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }
}
