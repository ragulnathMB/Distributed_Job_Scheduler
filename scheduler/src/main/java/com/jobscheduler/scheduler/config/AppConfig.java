package com.jobscheduler.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /** Used by the leader to push jobs to workers. Short timeouts so a dead worker never stalls a tick. */
    @Bean
    public RestTemplate workerRestTemplate() {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(2000);
        f.setReadTimeout(5000);
        return new RestTemplate(f);
    }
}
