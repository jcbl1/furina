package de.yelr.ai.furina.security;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class TokenBlacklistServiceTest {

    @Test
    void reportsActiveRevocation() {
        TokenBlacklistService service = new TokenBlacklistService();
        service.revoke("token", new Date(System.currentTimeMillis() + 60_000));

        assertThat(service.isRevoked("token")).isTrue();
        assertThat(service.isRevoked("other")).isFalse();
    }

    @Test
    void discardsRevocationsThatAlreadyExpired() {
        TokenBlacklistService service = new TokenBlacklistService();
        service.revoke("expired", new Date(System.currentTimeMillis() - 1_000));

        assertThat(service.isRevoked("expired")).isFalse();
    }
}
