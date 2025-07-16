package com.backend.security.service.aws;
import com.backend.security.model.aws.Aws;
import com.backend.security.repository.aws.AwsRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AwsService {

    private final AwsRepository awsRepository;

    public List<Aws> getAllAwsCredentials() {
        return awsRepository.findAll();
    }

    public Optional<Aws> getAwsCredentialById(int id) {
        return awsRepository.findById(id);
    }

    public Aws createAwsCredential(Aws aws) {
        return awsRepository.save(aws);
    }

public Aws updateAwsCredential(Aws updatedAws) {

    // Check if a matching credential exists (excluding the same ID)
    awsRepository.findByAccessKeyAndSecretKeyAndRegionAndProviderId(
            updatedAws.getAccessKey(),
            updatedAws.getSecretKey(),
            updatedAws.getRegion(),
            updatedAws.getProviderId()
    ).ifPresent(existing -> {
        if (existing.getId() != updatedAws.getId()) {
            System.out.println("error==========");
            throw new RuntimeException("AWS credential already exists with same accessKey, secretKey, region, and providerId");
        }
    });

    return awsRepository.findById(updatedAws.getId())
            .map(existing -> {
                existing.setUserId(updatedAws.getUserId());
                existing.setProviderId(updatedAws.getProviderId());
                existing.setAccessKey(updatedAws.getAccessKey());
                existing.setSecretKey(updatedAws.getSecretKey());
                existing.setRegion(updatedAws.getRegion());
                existing.setStatus(updatedAws.getStatus());
                return awsRepository.save(existing);
            })
            .orElseThrow(() -> new RuntimeException("AWS credential not found with id: " + updatedAws.getId()));
}


    public void deleteAwsCredential(int id) {
        awsRepository.deleteById(id);
    }
}

