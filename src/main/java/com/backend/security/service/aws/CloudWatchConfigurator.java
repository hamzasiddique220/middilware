package com.backend.security.service.aws;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.backend.security.config.AWSConfig;
import com.backend.security.config.AbstractCloudConfig;
import com.backend.security.repository.aws.AwsRepository;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;
import software.amazon.awssdk.services.ssm.model.SendCommandResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;
import software.amazon.awssdk.services.ssm.model.Target;

@Service
@Slf4j
public class CloudWatchConfigurator extends AbstractCloudConfig {

    public CloudWatchConfigurator(AWSConfig awsConfig, AwsRepository awsRepository) {
        super(awsConfig, awsRepository);
    }

    public void configureAndStartCloudWatchAgent(String instanceId, String userId) {
        AwsSsmClientclient(userId);
        try (SsmClient ssmClient = SsmClient.create()) {

            // Saare parameters ek hi map me daal do
            Map<String, List<String>> params = new HashMap<>();
            params.put("action", Arrays.asList("configure"));
            params.put("mode", Arrays.asList("ec2"));
            params.put("optionalConfigurationSource", Arrays.asList("ssm"));
            params.put("optionalConfigurationLocation", Arrays.asList("AmazonCloudWatch-linux"));
            params.put("optionalRestart", Arrays.asList("yes"));

            // SSM SendCommandRequest
            SendCommandRequest request = SendCommandRequest.builder()
                    .targets(Target.builder()
                            .key("InstanceIds")
                            .values(instanceId)
                            .build())
                    .documentName("AmazonCloudWatch-ManageAgent")
                    .comment("Configuring and starting CloudWatch Agent")
                    .parameters(params) // yahan ab single map pass hoga
                    .build();

            // SSM par command bhejna
            SendCommandResponse response = ssmClient.sendCommand(request);
            System.out.println("✅ CloudWatch Agent configuration initiated. Command ID: "
                    + response.command().commandId());

        } catch (SsmException e) {
            throw new RuntimeException("❌ Failed to configure/start CloudWatch Agent: "
                    + e.awsErrorDetails().errorMessage(), e);
        }
    }

    public void installCloudWatchAgent(String instanceId, String userId) {
        AwsSsmClientclient(userId);
        try (SsmClient ssmClient = SsmClient.create()) {

            // Parameters ka ek single map banana hoga
            Map<String, List<String>> params = new HashMap<>();
            params.put("action", Arrays.asList("Install"));
            params.put("name", Arrays.asList("AmazonCloudWatchAgent"));

            // SendCommandRequest create karo
            SendCommandRequest request = SendCommandRequest.builder()
                    .targets(Target.builder()
                            .key("InstanceIds")
                            .values(instanceId)
                            .build())
                    .documentName("AWS-ConfigureAWSPackage")
                    .comment("Installing Amazon CloudWatch Agent")
                    .parameters(params) // Single map pass karna hai
                    .build();

            // Command bhejna
            SendCommandResponse response = ssmClient.sendCommand(request);
            System.out.println("CloudWatch Agent installation initiated. Command ID: "
                    + response.command().commandId());

        } catch (SsmException e) {
            throw new RuntimeException("Failed to install CloudWatch Agent: "
                    + e.awsErrorDetails().errorMessage(), e);
        }
    }

}
