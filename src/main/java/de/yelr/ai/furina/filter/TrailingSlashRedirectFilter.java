package de.yelr.ai.furina.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Redirects paths that end with one or more slashes to their canonical form.
 * A 308 keeps the HTTP method and body intact for API clients.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TrailingSlashRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.length() > 1 && uri.endsWith("/")) {
            String trimmed = trimTrailingSlashes(uri);
            String query = request.getQueryString();
            String location = query == null ? trimmed : trimmed + "?" + query;
            response.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
            response.setHeader(HttpHeaders.LOCATION, location);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static String trimTrailingSlashes(String uri) {
        int end = uri.length();
        while (end > 1 && uri.charAt(end - 1) == '/') {
            end--;
        }
        return uri.substring(0, end);
    }
}
