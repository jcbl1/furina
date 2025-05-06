package de.yelr.ai.furina.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "username is required")
        @Size(max = 64, message = "username must not exceed 64 characters")
        String username,

        @NotBlank(message = "password is required")
        @Size(max = 128, message = "password must not exceed 128 characters")
        String password
) {
}
