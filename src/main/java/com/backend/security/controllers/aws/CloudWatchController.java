package com.backend.security.controllers.aws;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.security.model.user.User;
import com.backend.security.service.aws.CloudWatchConfigurator;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/ecs")
@RequiredArgsConstructor
public class CloudWatchController {

    private final CloudWatchConfigurator cloudWatchConfigurator;

    @GetMapping("/install-cloudwatch/{instanceId}")
    public String installCloudWatch(@PathVariable String instanceId) {
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cloudWatchConfigurator.installCloudWatchAgent(instanceId,user.getId());
        cloudWatchConfigurator.configureAndStartCloudWatchAgent(instanceId,user.getId());
        return "CloudWatch Agent installation and configuration started on " + instanceId;
    }
}
