package com.backend.security.controllers.monitor;

import com.backend.security.dto.aws.InstanceMetricsResponse;
import com.backend.security.model.user.User;
import com.backend.security.service.monitor.Ec2ResizeDecisionService;
import com.backend.security.service.monitor.MonitoringClient;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/monitor")
public class MonitoringController {

    private final MonitoringClient monitoringClient;

    private final Ec2ResizeDecisionService ec2ResizeDecisionService;

    public MonitoringController(MonitoringClient monitoringClient,Ec2ResizeDecisionService ec2ResizeDecisionService) {
        this.monitoringClient = monitoringClient;
        this.ec2ResizeDecisionService =ec2ResizeDecisionService;
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
    public InstanceMetricsResponse getInstanceMetrics(@RequestParam(defaultValue = "10") int day,@RequestParam(defaultValue = "day") String duration,@RequestParam(defaultValue = "null") String instanceId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return monitoringClient.getInstanceMetric(user.getId(),instanceId,day,duration);
    }
    @GetMapping("/ec2/can-resize/{instanceId}")
    public String checkIfCanResize(@PathVariable String instanceId) {
        boolean canResize = ec2ResizeDecisionService.canResizeToSmallerInstance(instanceId);
        return canResize
                ? "✅ Safe to downsize this instance."
                : "⚠️ Do NOT downsize — utilization is high.";
    }

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