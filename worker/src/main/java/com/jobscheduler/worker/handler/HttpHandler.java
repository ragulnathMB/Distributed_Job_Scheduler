package com.jobscheduler.worker.handler;

import com.jobscheduler.common.DispatchRequest;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/** Performs an HTTP GET; non-2xx fails the job. payload: {"url": "https://example.com"} */
@Component
public class HttpHandler implements JobHandler {

    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    @Override
    public String type() { return "HTTP"; }

    @Override
    public String execute(Map<String, Object> payload, DispatchRequest request) throws Exception {
        String url = JobHandler.str(payload, "url", null);
        if (url == null) {
            throw new IllegalArgumentException("payload.url is required");
        }
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException("GET " + url + " returned HTTP " + response.statusCode());
        }
        String body = response.body();
        return "HTTP " + response.statusCode() + " from " + url + " (" + body.length() + " bytes)";
    }
}
