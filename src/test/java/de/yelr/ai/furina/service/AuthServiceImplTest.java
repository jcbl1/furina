package de.yelr.ai.furina.service;

import de.yelr.ai.furina.config.properties.JwtProperties;
import de.yelr.ai.furina.dto.auth.LoginRequest;
import de.yelr.ai.furina.dto.auth.LoginResponse;
import de.yelr.ai.furina.exception.ApiException;
import de.yelr.ai.furina.security.AccountPrincipal;
import de.yelr.ai.furina.security.JwtTokenProvider;
import de.yelr.ai.furina.security.TokenBlacklistService;
import de.yelr.ai.furina.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private TokenBlacklistService blacklistService;

    private AuthServiceImpl authService;
    private AccountPrincipal principal;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties("test-secret", 3_600_000);
        authService = new AuthServiceImpl(authenticationManager, tokenProvider, properties, blacklistService);
        principal = new AccountPrincipal(9L, "admin", "hash", "ADMIN", true);
    }

    @Test
    void loginReturnsTokenAndUser() {
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                principal, null, principal.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenProvider.generateToken(principal)).thenReturn("jwt-token");

        LoginResponse response = authService.login(new LoginRequest("admin", "password"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3_600_000);
        assertThat(response.user().username()).isEqualTo("admin");
        assertThat(response.user().role()).isEqualTo("ADMIN");
    }

    @Test
    void validatesOnlyActiveTokens() {
        when(tokenProvider.isValid("good")).thenReturn(true);
        when(tokenProvider.isValid("bad")).thenReturn(false);
        when(blacklistService.isRevoked("good")).thenReturn(false);

        assertThat(authService.validateToken("good")).isTrue();
        assertThat(authService.validateToken("bad")).isFalse();
        assertThat(authService.validateToken(" ")).isFalse();
    }

    @Test
    void logoutRevokesValidToken() {
        Date expiration = new Date(System.currentTimeMillis() + 60_000);
        when(tokenProvider.isValid("token")).thenReturn(true);
        when(tokenProvider.extractExpiration("token")).thenReturn(expiration);
        when(blacklistService.isRevoked("token")).thenReturn(false);

        authService.logout("Bearer token");

        verify(blacklistService).revoke("token", expiration);
    }

    @Test
    void logoutRejectsMissingBearerToken() {
        assertThatThrownBy(() -> authService.logout("Basic abc"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("bearer token");
    }
}
