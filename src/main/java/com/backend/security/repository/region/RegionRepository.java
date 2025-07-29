package com.backend.security.repository.region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.security.model.region.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
    // You can add custom queries here if needed
}

