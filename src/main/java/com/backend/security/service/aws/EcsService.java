package com.backend.security.service.aws;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.backend.security.config.AWSConfig;
import com.backend.security.config.AbstractCloudConfig;
import com.backend.security.repository.aws.AwsRepository;
import com.backend.security.util.MessageType;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;


@Service
@Slf4j
public class EcsService extends AbstractCloudConfig {
    


     public EcsService(AWSConfig awsConfig, AwsRepository awsRepository) {
        super(awsConfig, awsRepository);
    }

     public ResponseEntity<?> listInstance(String userId) {
		try {
            AwsEc2Client(userId);
			log.debug("describe  vm");
			DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
			DescribeInstancesResponse vm = amazonEC2.describeInstances(request);
			if (vm.reservations().isEmpty()) {
				return response(MessageType.SUCCESS, HttpStatus.OK,
						"message", "instance fetch successfully",
						"result", vm.reservations());
			}
			return response(MessageType.SUCCESS, HttpStatus.OK,
					"message", "instance fetch successfully",
					"result", vm.reservations().get(0).instances());
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}
}
