package com.backend.security.controllers.aws;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.security.model.user.User;
import com.backend.security.service.aws.EcsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ecs")
@RequiredArgsConstructor
public class EcsController {

    private final EcsService ecsService;

    @GetMapping(value = "/list")
    public ResponseEntity<?> listInstance() {
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ecsService.listInstance(user.getId());
    }

}
