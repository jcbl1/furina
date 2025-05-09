package de.yelr.ai.furina.service.impl;

import de.yelr.ai.furina.config.properties.JwtProperties;
import de.yelr.ai.furina.dto.auth.CurrentUserResponse;
import de.yelr.ai.furina.dto.auth.LoginRequest;
import de.yelr.ai.furina.dto.auth.LoginResponse;
import de.yelr.ai.furina.exception.ApiException;
import de.yelr.ai.furina.security.AccountPrincipal;
import de.yelr.ai.furina.security.JwtTokenProvider;
import de.yelr.ai.furina.security.TokenBlacklistService;
import de.yelr.ai.furina.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;
    private final JwtProperties jwtProperties;
    private final TokenBlacklistService blacklistService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider,
                           JwtProperties jwtProperties,
                           TokenBlacklistService blacklistService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.jwtProperties = jwtProperties;
        this.blacklistService = blacklistService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        AccountPrincipal principal = (AccountPrincipal) authentication.getPrincipal();
        String token = tokenProvider.generateToken(principal);
        return new LoginResponse(token, "Bearer", jwtProperties.expiration(), toResponse(principal));
    }

    @Override
    public CurrentUserResponse currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AccountPrincipal principal)) {
            throw ApiException.unauthorized("Authentication is required");
        }
        return toResponse(principal);
    }

    @Override
    public boolean validateToken(String token) {
        return StringUtils.hasText(token)
                && tokenProvider.isValid(token)
                && !blacklistService.isRevoked(token);
    }

    @Override
    public void logout(String authorizationHeader) {
        String token = resolveBearer(authorizationHeader);
        if (token == null || !tokenProvider.isValid(token)) {
            throw ApiException.badRequest("A valid bearer token is required");
        }
        if (!blacklistService.isRevoked(token)) {
            blacklistService.revoke(token, tokenProvider.extractExpiration(token));
        }
    }

    private static String resolveBearer(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)
                || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private static CurrentUserResponse toResponse(AccountPrincipal principal) {
        return new CurrentUserResponse(principal.getId(), principal.getUsername(), principal.getRole());
    }
}
