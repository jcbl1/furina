package de.yelr.ai.furina.security;

import static org.assertj.core.api.Assertions.assertThat;

import de.yelr.ai.furina.config.properties.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

class JwtTokenProviderTest {

    private static final JwtProperties PROPERTIES = new JwtProperties("test-secret-value", 60_000);

    @Test
    void generatesAndReadsToken() {
        JwtTokenProvider provider = new JwtTokenProvider(PROPERTIES);
        String token =
                provider.generateToken(
                        User.withUsername("admin").password("ignored").roles("ADMIN").build());

        assertThat(provider.isValid(token)).isTrue();
        assertThat(provider.extractUsername(token)).isEqualTo("admin");
        assertThat(provider.extractExpiration(token)).isAfter(new java.util.Date());
    }

    @Test
    void rejectsMalformedAndExpiredTokens() {
        JwtTokenProvider provider = new JwtTokenProvider(PROPERTIES);
        assertThat(provider.isValid("not-a-token")).isFalse();
        assertThat(provider.isValid("a.b.c")).isFalse();

        JwtTokenProvider expiredProvider =
                new JwtTokenProvider(new JwtProperties("test-secret-value", -1_000));
        String expired =
                expiredProvider.generateToken(
                        User.withUsername("user").password("ignored").roles("USER").build());
        assertThat(expiredProvider.isValid(expired)).isFalse();
    }
}
