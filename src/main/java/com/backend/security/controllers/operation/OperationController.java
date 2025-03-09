package com.backend.security.controllers.operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.security.model.operation.Operation;
import com.backend.security.model.user.User;
import com.backend.security.service.operation.OperationService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/backend/api/v1/operation")
// @PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class OperationController {

    private final OperationService operationService;
    @PostMapping("/start")
    public ResponseEntity<?> operation(@RequestBody Operation operation){
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(operationService.start(operation,user.getId()));
        
    }
    
}
