package com.backend.security.controllers.region;
import com.backend.security.model.provider.Provider;
import com.backend.security.model.region.Region;
import com.backend.security.service.region.RegionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/region")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Region>> getAllRegions() {
        return ResponseEntity.ok(regionService.getAllRegions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Region> getRegionById(@PathVariable int id) {
        return regionService.getRegionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Region> createRegion(@RequestBody Region region) {
        return ResponseEntity.ok(regionService.createRegion(region));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Region> updateRegion(@PathVariable int id, @RequestBody Region region) {
        return ResponseEntity.ok(regionService.updateRegion(id, region));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProvider(@PathVariable int id) {
        regionService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }
}
