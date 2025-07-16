package com.backend.security.controllers.aws;

import com.backend.security.model.aws.Aws;
import com.backend.security.service.aws.AwsService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/aws")
@RequiredArgsConstructor
public class AwsController {

    private final AwsService awsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Aws>> getAllAwsCredentials() {
        return ResponseEntity.ok(awsService.getAllAwsCredentials());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Aws> getAwsCredentialById(@PathVariable int id) {
        return awsService.getAwsCredentialById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Aws> createAwsCredential(@RequestBody Aws aws) {
        return ResponseEntity.ok(awsService.createAwsCredential(aws));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Aws> updateAwsCredential(@RequestBody Aws aws) {
        return ResponseEntity.ok(awsService.updateAwsCredential(aws));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAwsCredential(@PathVariable int id) {
        awsService.deleteAwsCredential(id);
        return ResponseEntity.noContent().build();
    }
}
