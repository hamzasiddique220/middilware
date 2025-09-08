package com.backend.security.dto.aws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstanceMetricsResponse {
    private String instanceId;
    private List<Map<String, Object>> cpuData;
    private List<Map<String, Object>> memoryData;
    private List<Map<String, Object>> diskData;
    private double avgCpu;
    private double avgMemory;
    private double avgDisk;
    private String suggestion;
}