package com.backend.security.repository.aws;

import com.backend.security.model.aws.Aws;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AwsRepository extends JpaRepository<Aws, Integer> {
    Optional<Aws> findByAccessKeyAndSecretKeyAndRegionAndProviderId(
            String accessKey,
            String secretKey,
            String region,
            int providerId);
}
