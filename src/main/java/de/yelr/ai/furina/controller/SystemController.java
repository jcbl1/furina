package de.yelr.ai.furina.controller;

import de.yelr.ai.furina.common.ApiResponse;
import de.yelr.ai.furina.dto.system.SystemStatusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

    private final String applicationName;

    public SystemController(@Value("${spring.application.name:furina}") String applicationName) {
        this.applicationName = applicationName;
    }

    @GetMapping("/health")
    public ApiResponse<SystemStatusResponse> health() {
        return ApiResponse.success(new SystemStatusResponse("UP", applicationName, Instant.now()));
    }
}
