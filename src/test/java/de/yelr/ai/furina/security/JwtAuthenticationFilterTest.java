package de.yelr.ai.furina.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenBlacklistService blacklistService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesValidToken() throws Exception {
        AccountPrincipal principal = new AccountPrincipal(1L, "user", "hash", "USER", true);
        when(tokenProvider.isValid("valid-token")).thenReturn(true);
        when(tokenProvider.extractUsername("valid-token")).thenReturn("user");
        when(blacklistService.isRevoked("valid-token")).thenReturn(false);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(principal);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenProvider, userDetailsService, blacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/foods");
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("user");
        verify(chain).doFilter(request, response);
    }

    @Test
    void ignoresRevokedToken() throws Exception {
        when(blacklistService.isRevoked("revoked")).thenReturn(true);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenProvider, userDetailsService, blacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/foods");
        request.addHeader("Authorization", "Bearer revoked");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void ignoresMissingToken() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenProvider, userDetailsService, blacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/system/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }
}
