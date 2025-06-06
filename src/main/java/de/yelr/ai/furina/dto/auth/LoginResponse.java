package de.yelr.ai.furina.dto.auth;

public record LoginResponse(
        String token, String tokenType, long expiresIn, CurrentUserResponse user) {}
