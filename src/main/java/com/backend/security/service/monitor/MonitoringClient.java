package com.backend.security.service.monitor;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MonitoringClient {

    private final WebClient webClient;

    public MonitoringClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://second-service").build();
    }

    @Retry(name = "monitorRetry", fallbackMethod = "fallbackHandler")
    @CircuitBreaker(name = "monitorCB", fallbackMethod = "fallbackHandler")
    public Mono<List<Map<String, Object>>> getInstanceMetricsAsync() {
        return getGenericMetrics("/monitor/instances");
    }

    @Retry(name = "monitorRetry", fallbackMethod = "fallbackHandler")
    @CircuitBreaker(name = "monitorCB", fallbackMethod = "fallbackHandler")
    public Mono<List<Map<String, Object>>> getVolumeMetricsAsync() {
        return getGenericMetrics("/monitor/volume");
    }

    @Retry(name = "monitorRetry", fallbackMethod = "fallbackHandler")
    @CircuitBreaker(name = "monitorCB", fallbackMethod = "fallbackHandler")
    public Mono<List<Map<String, Object>>> getInstanceByDayMetricsAsync(int days) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/monitor/instances")
                        .queryParam("days", days)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .timeout(Duration.ofSeconds(5));
    }

    private Mono<List<Map<String, Object>>> getGenericMetrics(String uriPath) {
        return webClient.get()
                .uri(uriPath)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .timeout(Duration.ofSeconds(5));
    }

    public Mono<List<Map<String, Object>>> fallbackHandler(int days, Throwable t) {
        System.out.println("Fallback triggered for days=" + days + ": " + t.getMessage());
        return Mono.just(Collections.emptyList());
    }

    public Mono<List<Map<String, Object>>> fallbackHandler(String uriPath, Throwable t) {
        System.out.println("Fallback for " + uriPath + ": " + t.getMessage());
        return Mono.just(Collections.emptyList());
    }

    public Mono<List<Map<String, Object>>> fallbackHandler(Throwable t) {
        System.out.println("Fallback triggered: " + t.getMessage());
        return Mono.just(Collections.emptyList());
    }
}
