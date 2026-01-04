package com.tyreplus.dealer.infrastructure.security;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ProxyManager<String> proxyManager;


    public RateLimitingFilter(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        String uri = request.getRequestURI();

        // Choose limit based on URL
        BucketConfiguration config;
        if (uri.contains("/api/v1/auth/otp")) {
            // Strict: 5 per minute
            config = BucketConfiguration.builder()
                    .addLimit(limit -> limit.capacity(5).refillGreedy(5, Duration.ofMinutes(1)))
                    .build();
        } else {
            // General: 100 per minute for all other APIs
            config = BucketConfiguration.builder()
                    .addLimit(limit -> limit.capacity(100).refillGreedy(100, Duration.ofMinutes(1)))
                    .build();
        }

        Bucket bucket = proxyManager.builder().build(clientIp, () -> config);

        if (!bucket.tryConsume(1)) {
            response.setStatus(429);
            response.getWriter().write("Too many requests. Please slow down.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
