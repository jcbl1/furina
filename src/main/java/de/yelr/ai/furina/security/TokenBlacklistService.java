package de.yelr.ai.furina.security;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * In-memory revocation list. Expired entries are removed lazily, which keeps the structure bounded
 * by the number of tokens issued within one lifetime. A shared store is the natural next step when
 * the service is scaled out.
 */
@Service
public class TokenBlacklistService {

    private final Map<String, Long> revokedTokens = new ConcurrentHashMap<>();

    public void revoke(String token, Date expiration) {
        if (token != null && expiration != null) {
            revokedTokens.put(token, expiration.getTime());
        }
    }

    public boolean isRevoked(String token) {
        if (token == null) {
            return false;
        }
        Long expiresAt = revokedTokens.get(token);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt <= System.currentTimeMillis()) {
            revokedTokens.remove(token);
            return false;
        }
        return true;
    }
}
