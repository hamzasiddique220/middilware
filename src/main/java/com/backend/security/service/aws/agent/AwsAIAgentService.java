package com.backend.security.service.aws.agent;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.backend.security.config.AWSConfig;
import com.backend.security.config.AbstractCloudConfig;
import com.backend.security.repository.aws.AwsRepository;
import software.amazon.awssdk.services.ec2.model.*;

@Service
@Slf4j
public class AwsAIAgentService extends AbstractCloudConfig {

    public AwsAIAgentService(AWSConfig awsConfig, AwsRepository awsRepository) {
        super(awsConfig, awsRepository);
    }

    public String createVm(int cpu, int ram,String userId) {
        DescribeVpcsRequest vpcRequest = DescribeVpcsRequest.builder()
                .filters(Filter.builder()
                        .name("isDefault")
                        .values("true")
                        .build())
                .build();

        DescribeVpcsResponse vpcResponse = amazonEC2.describeVpcs(vpcRequest);
        if (vpcResponse.vpcs().isEmpty()) {
            throw new RuntimeException("No default VPC found!");
        }

        String vpcId = vpcResponse.vpcs().get(0).vpcId();

        // Step 2: Get a default subnet from the default VPC
        DescribeSubnetsRequest subnetRequest = DescribeSubnetsRequest.builder()
                .filters(Filter.builder()
                        .name("vpc-id")
                        .values(vpcId)
                        .build())
                .build();

        DescribeSubnetsResponse subnetResponse = amazonEC2.describeSubnets(subnetRequest);
        if (subnetResponse.subnets().isEmpty()) {
            throw new RuntimeException("No subnets found for VPC: " + vpcId);
        }
        // String latestImageId = null;
        // List<Image> images = fetchUbuntuImages("gp3", "20.04");

        // if (!images.isEmpty()) {
        // Image latestImage = images.get(0);
        // System.out.println("Latest Ubuntu AMI: " + latestImage.imageId());
        // System.out.println("Name: " + latestImage.name());
        // latestImageId=latestImage.imageId();

        // } else {
        // System.out.println("No matching images found.");
        // }
        String instanceType = getInstanceType(cpu, ram);
        String subnetId = getDefaultSubnetId();
        RunInstancesRequest request = RunInstancesRequest.builder()
                .imageId("ami-00a01ce0d76eb2335") // Replace with a valid AMI ID
                .instanceType(instanceType) // Or dynamically choose based on CPU/RAM
                .minCount(1)
                .maxCount(1)
                .subnetId(subnetId)
                .build();

        RunInstancesResponse response = amazonEC2.runInstances(request);
        return response.instances().get(0).instanceId();
    }

    private String getInstanceType(int cpu, int ram) {
        // Key = "cpu-ram" format
        Map<String, String> flavorMap = new HashMap<>();
        flavorMap.put("1-1", "t3.nano"); // 2 vCPUs, 4 GB RAM
        flavorMap.put("1-2", "t3.micro"); // 2 vCPUs, 4 GB RAM
        flavorMap.put("2-4", "t3.medium"); // 2 vCPUs, 4 GB RAM
        flavorMap.put("2-8", "t3.large"); // 2 vCPUs, 8 GB RAM
        flavorMap.put("4-16", "t3.xlarge"); // 4 vCPUs, 16 GB RAM
        flavorMap.put("8-32", "t3.2xlarge"); // 8 vCPUs, 32 GB RAM

        String key = cpu + "-" + ram;
        return flavorMap.getOrDefault(key, "t3.micro"); // Default fallback
    }

    // public List<Image> fetchUbuntuImages(String architecture, String storageType)
    // {
    // // Filter for Ubuntu AMIs only
    // DescribeImagesRequest request = DescribeImagesRequest.builder()
    // .owners("099720109477") // Canonical's official Ubuntu AMI owner ID
    // .filters(
    // Filter.builder().name("name").values("ubuntu/images/hvm-ssd/ubuntu-*").build(),
    // Filter.builder().name("architecture").values(architecture).build(),
    // Filter.builder().name("root-device-type").values(storageType).build(),
    // Filter.builder().name("state").values("available").build()
    // )
    // .build();

    // DescribeImagesResponse response = amazonEC2.describeImages(request);

    // List<Image> images = response.images();
    // // Sort by creation date to get the latest image first
    // images.sort((img1, img2) ->
    // img2.creationDate().compareTo(img1.creationDate()));

    // return images;
    // }

    public List<Image> fetchUbuntuImages(String storageType, String version) {
        DescribeImagesRequest request = DescribeImagesRequest.builder()
                .owners("099720109477") // Canonical Ubuntu owner ID
                .filters(
                        Filter.builder()
                                .name("name")
                                .values("ubuntu/images/hvm-ssd/ubuntu-" + version + "*")
                                .build(),
                        Filter.builder()
                                .name("root-device-type")
                                .values("ebs")
                                .build(),
                        Filter.builder()
                                .name("virtualization-type")
                                .values("hvm")
                                .build(),
                        Filter.builder()
                                .name("block-device-mapping.volume-type")
                                .values(storageType) // gp2 or gp3
                                .build())
                .build();

        DescribeImagesResponse response = amazonEC2.describeImages(request);

        // Sort images by creation date (latest first)
        List<Image> images = response.images().stream()
                .sorted((a, b) -> b.creationDate().compareTo(a.creationDate()))
                .toList();

        return images;
    }

    public String getDefaultSubnetId() {
        DescribeSubnetsResponse response = amazonEC2.describeSubnets();
        return response.subnets().get(0).subnetId();
    }

    public String attachVolume(String instanceId, String volumeId,String userId) {
        AwsEc2Client(userId);
        AttachVolumeRequest request = AttachVolumeRequest.builder()
                .volumeId(volumeId)
                .instanceId(instanceId)
                .device("/dev/sdf")
                .build();
        amazonEC2.attachVolume(request);
        return "Volume attached successfully!";
    }

    public String startVm(String instanceId,String userId) {
        AwsEc2Client(userId);
        StartInstancesRequest request = StartInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();
        amazonEC2.startInstances(request);
        return "VM started successfully!";
    }

    public String stopVm(String instanceId,String userId) {
        AwsEc2Client(userId);
        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();
        amazonEC2.stopInstances(request);
        return "VM stopped successfully!";
    }

    public String deleteVm(String instanceId,String userId) {
        AwsEc2Client(userId);
        TerminateInstancesRequest request = TerminateInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();
        amazonEC2.terminateInstances(request);
        return "VM deleted successfully!";
    }

    public ResponseEntity<?> listVm(String userId) {
        AwsEc2Client(userId);
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
        DescribeInstancesResponse response = amazonEC2.describeInstances(request);

        if (response.reservations().isEmpty()) {
            return ResponseEntity.ok("{\"message\": \"No instances found\"}");
        }

        // Collect all instances from all reservations
        List<Object> instances = new ArrayList<>();
        for (Reservation reservation : response.reservations()) {
            instances.addAll(reservation.instances());
        }

        // Convert list of instances to JSON
        String json = gson.toJson(instances);

        return ResponseEntity.ok(json);
    }
}
