package com.backend.security.controllers.monitor;

import com.backend.security.service.monitor.MonitoringClient;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor")
public class MonitoringController {

    private final MonitoringClient monitoringClient;

    public MonitoringController(MonitoringClient monitoringClient) {
        this.monitoringClient = monitoringClient;
    }

    @GetMapping("/monitoring-data")
    public Mono<ResponseEntity<List<Map<String, Object>>>> fetchMonitoringData() {
        return monitoringClient.getInstanceMetricsAsync()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/monitoring-data-days")
    public Mono<ResponseEntity<List<Map<String, Object>>>> fetchMonitoringData(
            @RequestParam(defaultValue = "7") int days) {
        return monitoringClient.getInstanceByDayMetricsAsync(days)
                .map(ResponseEntity::ok);
    }

}