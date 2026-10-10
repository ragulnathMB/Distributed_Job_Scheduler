package com.jobscheduler.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Talks to the scheduler cluster. All scheduler nodes are equivalent for these calls (state lives in PostgreSQL),
 * so we simply fail over to the next URL when one is unreachable.
 */
@Component
public class SchedulerClient {
    private static final Logger log = LoggerFactory.getLogger(SchedulerClient.class);

    private final List<String> urls;
    private final RestTemplate rest;
    private final AtomicInteger preferred = new AtomicInteger();

    public SchedulerClient(@Value("${worker.scheduler-urls}") String urls, RestTemplate rest) {
        this.urls = Arrays.stream(urls.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
        this.rest = rest;
    }

    /** POSTs to the first reachable scheduler. Returns the HTTP status, or -1 if none could be reached. */
    public int post(String path, Object body) {
        for (int i = 0; i < urls.size(); i++) {
            int idx = (preferred.get() + i) % urls.size();
            String url = urls.get(idx) + path;
            try {
                int status = rest.postForEntity(url, body, Void.class).getStatusCode().value();
                preferred.set(idx);
                return status;
            } catch (HttpClientErrorException e) {
                preferred.set(idx);
                return e.getStatusCode().value();       // 4xx: the scheduler answered, don't try others
            } catch (Exception e) {
                log.warn("Scheduler {} unavailable: {}", urls.get(idx), e.getMessage());
            }
        }
        return -1;
    }
}
