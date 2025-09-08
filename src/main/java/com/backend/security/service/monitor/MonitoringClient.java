package com.backend.security.service.monitor;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.backend.security.config.AWSConfig;
import com.backend.security.config.AbstractCloudConfig;
import com.backend.security.dto.aws.InstanceMetricsResponse;
import com.backend.security.repository.aws.AwsRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ec2.model.Tag;

@Service
@Slf4j
public class MonitoringClient extends AbstractCloudConfig {


    private final WebClient webClient;

    public MonitoringClient(WebClient.Builder webClientBuilder,
                            AWSConfig awsConfig,
                            AwsRepository awsRepository) {
        super(awsConfig, awsRepository);
        this.webClient = webClientBuilder.baseUrl("http://second-service").build();
    }

    // Required for super()

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
        System.out.println("Fallback triggered for days=" + days + ": " +
                t.getMessage());
        return Mono.just(Collections.emptyList());
    }

    public Mono<List<Map<String, Object>>> fallbackHandler(String uriPath,
            Throwable t) {
        System.out.println("Fallback for " + uriPath + ": " + t.getMessage());
        return Mono.just(Collections.emptyList());
    }

    public Mono<List<Map<String, Object>>> fallbackHandler(Throwable t) {
        System.out.println("Fallback triggered: " + t.getMessage());
        return Mono.just(Collections.emptyList());
    }

    public static int convertToPeriodInSeconds(String type) {
        if (type.equalsIgnoreCase("day")) {
            return 1 * 24 * 60 * 60; // 1 day → 86400 seconds
        } else {
            return 1 * 60 * 60; // 1 hour → 3600 seconds
        }
    }

    public InstanceMetricsResponse getInstanceMetric(String userId, String instanceId, int day,String duration) {
        AwsEc2Client(userId);
        AwsCloudWatchclient(userId);
        int time = convertToPeriodInSeconds(duration);
        int totalVcpus = getInstanceVcpus(instanceId);
        List<Map<String, Object>> cpuData =getMetricData(instanceId, time,"CPUUtilization","AWS/EC2",day,totalVcpus);
         List<Map<String, Object>> memoryData =  getMetricData(instanceId, time,"mem_used_percent","CWAgent",day,null);
        List<Map<String, Object>> diskData = getMetricData(instanceId, time,"disk_used_percent","CWAgent",day,null);
                // Calculate average values
        double avgCpu = cpuData.stream().mapToDouble(d -> (Double) d.get("value")).average().orElse(0.0);
        double avgMemory = memoryData.stream().mapToDouble(d -> (Double) d.get("value")).average().orElse(0.0);
        double avgDisk = diskData.stream().mapToDouble(d -> (Double) d.get("value")).average().orElse(0.0);
                String suggestion;
        if (avgCpu < 30 && avgMemory < 40 && avgDisk < 60) {
            suggestion = "✅ Safe to downsize instance — low resource usage.";
        } else if (avgCpu > 80 || avgMemory > 85 || avgDisk > 85) {
            suggestion = "⚠️ High resource usage — consider upgrading instance.";
        } else {
            suggestion = "ℹ️ Current instance size is optimal — no change needed.";
        }
        return InstanceMetricsResponse.builder()
                .instanceId(instanceId)
                .cpuData(cpuData)
                .memoryData(memoryData)
                .diskData(diskData)
                .avgCpu(avgCpu)
                .avgMemory(avgMemory)
                .avgDisk(avgDisk)
                .suggestion(suggestion)
                .build();

    }

  public List<Map<String, Object>> getMetricData(String instanceId, int time, String metricName,
                                                  String namespace, int day, Integer totalVcpus) {

        MetricDataQuery q = MetricDataQuery.builder()
                .id("metric")
                .metricStat(MetricStat.builder()
                        .period(time) // e.g., 86400 for daily average
                        .stat("Average")
                        .metric(Metric.builder()
                                .namespace(namespace)
                                .metricName(metricName)
                                .dimensions(Dimension.builder()
                                        .name("InstanceId")
                                        .value(instanceId)
                                        .build())
                                .build())
                        .build())
                .returnData(true)
                .build();

        Instant endDate = Instant.now();
        Instant startDate = endDate.minusSeconds(86400L * day);

        GetMetricDataResponse resp = cloudWatchClient.getMetricData(GetMetricDataRequest.builder()
                .startTime(startDate)
                .endTime(endDate)
                .metricDataQueries(q)
                .scanBy(ScanBy.TIMESTAMP_ASCENDING)
                .build());

        List<Map<String, Object>> results = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault());

        for (MetricDataResult r : resp.metricDataResults()) {
            List<Instant> timestamps = r.timestamps();
            List<Double> values = r.values();

            for (int i = 0; i < values.size(); i++) {
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("date", formatter.format(timestamps.get(i)));
                data.put("value", Math.round(values.get(i) * 100.0) / 100.0);

                // If CPU metric, calculate used vCPUs
                if ("CPUUtilization".equals(metricName) && totalVcpus != null) {
                    double usedVcpus = (values.get(i) / 100.0) * totalVcpus;
                    data.put("usedVcpus", Math.round(usedVcpus * 100.0) / 100.0);
                }

                results.add(data);
            }
        }

        log.info("Metric [{}] daily averages for instance {} (last {} days) -> {}", metricName, instanceId, day, results);
        return results;
    }

        private int getInstanceVcpus(String instanceId) {
        DescribeInstancesResponse response = amazonEC2.describeInstances(
            DescribeInstancesRequest.builder().instanceIds(instanceId).build()
        );

        // Get instance type (e.g., t3.medium, m5.large, etc.)
        String instanceType = response.reservations().get(0).instances().get(0).instanceTypeAsString();

        // Fetch instance type details to get vCPUs
        DescribeInstanceTypesResponse typeResponse = amazonEC2.describeInstanceTypes(
            DescribeInstanceTypesRequest.builder().instanceTypesWithStrings(instanceType).build()
        );

        return typeResponse.instanceTypes().get(0).vCpuInfo().defaultVCpus();
    }

    private double getAverageMetric(String instanceId, String metricName, String namespace, String unit, int days) {
        GetMetricStatisticsRequest request = GetMetricStatisticsRequest.builder()
                .namespace(namespace)
                .metricName(metricName)
                .dimensions(Dimension.builder().name("InstanceId").value(instanceId).build())
                .startTime(Instant.now().minusSeconds(86400L * days))
                .endTime(Instant.now())
                .period(3600)
                .statistics(Statistic.AVERAGE)
                .unit(StandardUnit.fromValue(unit))
                .build();

        GetMetricStatisticsResponse response = cloudWatchClient.getMetricStatistics(request);
        List<Datapoint> datapoints = response.datapoints();

        return datapoints.stream()
                .mapToDouble(Datapoint::average)
                .average()
                .orElse(0.0);
    }

    public List<Volume> listAllVolumes() {
        DescribeVolumesResponse response = amazonEC2.describeVolumes(DescribeVolumesRequest.builder().build());
        return response.volumes();
    }

    public double calculateVolumeCost(Volume volume) {
        int size = volume.size(); // in GB
        String type = volume.volumeTypeAsString();

        double pricePerGb = switch (type) {
            case "gp2", "gp3" -> 0.08;
            case "io1", "io2" -> 0.125;
            case "st1" -> 0.045;
            case "sc1" -> 0.025;
            case "standard" -> 0.05;
            default -> 0.08;
        };

        return size * pricePerGb;
    }

    public void generateVolumeOptimizationReport(String userId) {
        AwsEc2Client(userId);
        AwsCloudWatchclient(userId);
        List<Volume> volumes = listAllVolumes();

        for (Volume vol : volumes) {
            double cost = calculateVolumeCost(vol);
            boolean idle = isIdle(vol.volumeId());

            System.out.println("Volume: " + vol.volumeId());
            System.out.println("Type: " + vol.volumeType());
            System.out.println("Size: " + vol.size() + " GB");
            System.out.println("Monthly Cost: $" + cost);
            System.out.println("Is Idle: " + idle);
            System.out.println("Attached To: " +
                    (vol.attachments().isEmpty() ? "Not Attached" : vol.attachments().get(0).instanceId()));
            System.out.println("-----");
        }
    }

    public boolean isIdle(String volumeId) {
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(7, ChronoUnit.DAYS);

        boolean noReadOps = isMetricZero(volumeId, "VolumeReadOps", startTime, endTime);
        boolean noWriteOps = isMetricZero(volumeId, "VolumeWriteOps", startTime, endTime);

        return noReadOps && noWriteOps;
    }

    private boolean isMetricZero(String volumeId, String metricName, Instant startTime, Instant endTime) {
        GetMetricStatisticsRequest request = GetMetricStatisticsRequest.builder()
                .namespace("AWS/EBS")
                .metricName(metricName)
                .dimensions(Dimension.builder().name("VolumeId").value(volumeId).build())
                .startTime(startTime)
                .endTime(endTime)
                .period(86400)
                .statistics(Statistic.SUM)
                .build();

        List<Datapoint> dataPoints = cloudWatchClient.getMetricStatistics(request).datapoints();
        return dataPoints.stream().noneMatch(dp -> dp.sum() != null && dp.sum() > 0.0);
    }

    public List<Instance> getRunningInstances() {
        List<Instance> allInstances = new ArrayList<>();
        DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                .filters(Filter.builder().name("instance-state-name").values("running").build())
                .build();

        amazonEC2.describeInstancesPaginator(request).reservations()
                .forEach(res -> allInstances.addAll(res.instances()));

        return allInstances;
    }

    public boolean isUnderUtilized(String instanceId) {
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(7, ChronoUnit.DAYS);

        GetMetricStatisticsRequest request = GetMetricStatisticsRequest.builder()
                .namespace("AWS/EC2")
                .metricName("CPUUtilization")
                .dimensions(Dimension.builder().name("InstanceId").value(instanceId).build())
                .startTime(startTime)
                .endTime(endTime)
                .period(86400)
                .statistics(Statistic.AVERAGE)
                .build();

        List<Datapoint> datapoints = cloudWatchClient.getMetricStatistics(request).datapoints();
        if (datapoints.isEmpty())
            return false;

        double avgCpu = datapoints.stream()
                .mapToDouble(Datapoint::average)
                .average().orElse(0.0);

        return avgCpu < 10.0;
    }

    public void reportUnderutilizedInstances(String userId) {
        AwsEc2Client(userId);
        AwsCloudWatchclient(userId);
        List<Instance> instances = getRunningInstances();

        for (Instance instance : instances) {
            if (isUnderUtilized(instance.instanceId())) {
                System.out.println("Underutilized Instance Found:");
                System.out.println("ID: " + instance.instanceId());
                System.out.println("Type: " + instance.instanceTypeAsString());
                System.out.println("Launch Time: " + instance.launchTime());
                System.out.println("-----");
            }
        }
    }

    public void generateOptimizationRecommendations(String userId) {
        AwsEc2Client(userId);
        AwsCloudWatchclient(userId);
        Map<String, Double> hourlyCosts = Map.of(
                "m5.4xlarge", 0.768,
                "t3.medium", 0.0416,
                "m5.large", 0.096,
                "t3.large", 0.0832);

        List<Instance> instances = getRunningInstances();

        for (Instance instance : instances) {
            String instanceId = instance.instanceId();
            String currentType = instance.instanceTypeAsString();

            if (!isUnderUtilized(instanceId))
                continue;

            String suggestedType = "t3.medium";

            double currentMonthlyCost = hourlyCosts.getOrDefault(currentType, 0.0) * 24 * 30;
            double suggestedMonthlyCost = hourlyCosts.getOrDefault(suggestedType, 0.0) * 24 * 30;
            double saving = currentMonthlyCost - suggestedMonthlyCost;

            if (saving > 0) {
                System.out.println("💡 Recommendation:");
                System.out.println("Replace " + currentType + " with " + suggestedType);
                System.out.println("Instance ID: " + instanceId);
                System.out.printf("Monthly Cost: $%.2f → $%.2f%n", currentMonthlyCost, suggestedMonthlyCost);
                System.out.printf("Estimated Monthly Saving: $%.2f%n", saving);
                System.out.println("------");
            }
        }
    }

     public Double getAverageCPUUtilization(String instanceId) {
        Instant endTime = Instant.now();
        Instant startTime = endTime.minusSeconds(10 * 24 * 60 * 60); // last 10 days

        GetMetricStatisticsRequest request = GetMetricStatisticsRequest.builder()
                .namespace("AWS/EC2")
                .metricName("CPUUtilization")
                .dimensions(Dimension.builder().name("InstanceId").value(instanceId).build())
                .startTime(startTime)
                .endTime(endTime)
                .period(3600) // 1-hour period
                .statistics(Statistic.AVERAGE)
                .build();

        GetMetricStatisticsResponse response = cloudWatchClient.getMetricStatistics(request);
        List<Datapoint> datapoints = response.datapoints();

        return datapoints.stream()
                .mapToDouble(Datapoint::average)
                .average()
                .orElse(0.0);
    }

    public Double getAverageMemoryUtilization(String instanceId) {
        Instant endTime = Instant.now();
        Instant startTime = endTime.minusSeconds(10 * 24 * 60 * 60);

        GetMetricStatisticsRequest request = GetMetricStatisticsRequest.builder()
                .namespace("CWAgent")  // Memory metrics come from CloudWatch Agent
                .metricName("mem_used_percent")
                .dimensions(Dimension.builder().name("InstanceId").value(instanceId).build())
                .startTime(startTime)
                .endTime(endTime)
                .period(3600)
                .statistics(Statistic.AVERAGE)
                .build();

        GetMetricStatisticsResponse response = cloudWatchClient.getMetricStatistics(request);
        List<Datapoint> datapoints = response.datapoints();

        return datapoints.stream()
                .mapToDouble(Datapoint::average)
                .average()
                .orElse(0.0);
    }
}
