package com.backend.security.controllers.monitor;

import com.backend.security.model.user.User;
import com.backend.security.service.monitor.MonitoringClient;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/monitor")
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

    @GetMapping("/instances/spec")
    public Map<String, Object> getInstanceMetrics(@RequestParam(defaultValue = "10") int day,@RequestParam(defaultValue = "day") String duration,@RequestParam(defaultValue = "null") String instanceId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return monitoringClient.getAllInstanceStats(user.getId(),instanceId,day,duration);
    }

    // @GetMapping("/volume")
    // public List<Map<String, Object>> getvolumeMetrics() {
    // return monitoringClient.getvolumeStats();
    // }

    @GetMapping("/volumes/optimization-report")
    public void generateVolumeOptimizationReport() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        monitoringClient.generateVolumeOptimizationReport(user.getId());
    }

    @GetMapping("/instances/underutilized")
    public void reportUnderutilizedInstances() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        monitoringClient.reportUnderutilizedInstances(user.getId());
    }

    @GetMapping("/instances/recommendations")
    public void generateOptimizationRecommendations() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        monitoringClient.generateOptimizationRecommendations(user.getId());
    }

}