package com.backend.security.config;

import com.backend.security.model.aws.Aws;
import com.backend.security.repository.aws.AwsRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.ec2.Ec2Client;

@RequiredArgsConstructor
public abstract class AbstractCloudConfig {

    protected final AWSConfig awsConfig;
    protected final AwsRepository awsRepository;

    protected Ec2Client amazonEC2;
    protected CloudWatchClient cloudWatchClient;

    public void AwsEc2Client(String userId) {
        Aws aws = awsRepository.findByUserId(userId);
        amazonEC2 = awsConfig.getEc2Client(aws);
    }

    public void AwsCloudWatchclient(String userId) {
        Aws aws = awsRepository.findByUserId(userId);
        cloudWatchClient = awsConfig.getCloudWatchClient(aws);
    }
}
