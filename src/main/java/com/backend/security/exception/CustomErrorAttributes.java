package com.backend.security.exception;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Throwable error = getError(webRequest);
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("timestamp", LocalDateTime.now());

        // ✅ Handle AWS Ec2Exception explicitly
        if (error instanceof Ec2Exception ex) {
            attributes.put("status", ex.statusCode());
            attributes.put("error", "AWS EC2 Error");
            attributes.put("message", ex.awsErrorDetails() != null
                    ? ex.awsErrorDetails().errorMessage()
                    : ex.getMessage());
            attributes.put("requestId", ex.requestId());
            return attributes;
        }

        // ✅ Generic exceptions fallback
        attributes.put("status", 500);
        attributes.put("error", "Internal Server Error");
        attributes.put("message", error != null ? error.getMessage() : "Unexpected error");
        return attributes;
    }
}

