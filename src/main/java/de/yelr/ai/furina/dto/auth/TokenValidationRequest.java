package de.yelr.ai.furina.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record TokenValidationRequest(@NotBlank(message = "token is required") String token) {}
