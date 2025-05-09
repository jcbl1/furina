package de.yelr.ai.furina.service;

import de.yelr.ai.furina.dto.auth.CurrentUserResponse;
import de.yelr.ai.furina.dto.auth.LoginRequest;
import de.yelr.ai.furina.dto.auth.LoginResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    CurrentUserResponse currentUser(Authentication authentication);

    boolean validateToken(String token);

    void logout(String authorizationHeader);
}
