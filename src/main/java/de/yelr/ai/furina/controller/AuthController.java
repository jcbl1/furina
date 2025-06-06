package de.yelr.ai.furina.controller;

import de.yelr.ai.furina.common.ApiResponse;
import de.yelr.ai.furina.dto.auth.CurrentUserResponse;
import de.yelr.ai.furina.dto.auth.LoginRequest;
import de.yelr.ai.furina.dto.auth.LoginResponse;
import de.yelr.ai.furina.dto.auth.TokenValidationRequest;
import de.yelr.ai.furina.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(Authentication authentication) {
        return ApiResponse.success(authService.currentUser(authentication));
    }

    @PostMapping("/validate")
    public ApiResponse<Boolean> validate(@Valid @RequestBody TokenValidationRequest request) {
        return ApiResponse.success(authService.validateToken(request.token()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
                    String authorization) {
        authService.logout(authorization);
        return ApiResponse.success();
    }
}
