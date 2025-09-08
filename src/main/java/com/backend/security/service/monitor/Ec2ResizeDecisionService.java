package com.backend.security.service.monitor;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Ec2ResizeDecisionService {
    
        private final MonitoringClient monitoringClient;

        public boolean canResizeToSmallerInstance(String instanceId) {
        Double avgCpu = monitoringClient.getAverageCPUUtilization(instanceId);
        Double avgMemory = monitoringClient.getAverageMemoryUtilization(instanceId);

        System.out.println("Last 10 days Average CPU Utilization: " + avgCpu + "%");
        System.out.println("Last 10 days Average Memory Utilization: " + avgMemory + "%");

        // Decision: If both CPU and memory usage < 40%, we can safely downsize
        return avgCpu < 40 && avgMemory < 40;
    }
}
