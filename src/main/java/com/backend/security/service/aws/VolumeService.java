package com.backend.security.service.aws;

import org.springframework.stereotype.Service;

import com.backend.security.config.AWSConfig;
import com.backend.security.config.AbstractCloudConfig;
import com.backend.security.repository.aws.AwsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VolumeService extends AbstractCloudConfig {
    public VolumeService(AWSConfig awsConfig, AwsRepository awsRepository) {
        super(awsConfig, awsRepository);
    }

}
