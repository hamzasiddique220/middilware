package com.backend.security.service.region;
import com.backend.security.model.region.Region;
import com.backend.security.repository.region.RegionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    public Optional<Region> getRegionById(int id) {
        return regionRepository.findById(id);
    }

    public Region createRegion(Region region) {
        return regionRepository.save(region);
    }

    public Region updateRegion(int id, Region updatedRegion) {
        return regionRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedRegion.getName());
                    existing.setStatus(updatedRegion.getStatus());
                    existing.setDisplayName(updatedRegion.getDisplayName());
                    return regionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Region not found with id: " + id));
    }

    public void deleteProvider(int id) {
        regionRepository.deleteById(id);
    }
}

