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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ProxyManager<String> proxyManager;

    public RateLimitingFilter(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        RateLimitType type = resolveRateLimitType(request);
        String key = resolveRateLimitKey(request, type);

        Bucket bucket = proxyManager.builder()
                .build(key, () -> resolveConfig(type));

        if (!bucket.tryConsume(1)) {
            response.setStatus(429);
            response.getWriter().write("Too many requests. Please slow down.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // ---------------- HELPERS ----------------

    private RateLimitType resolveRateLimitType(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (uri.startsWith("/api/v1/auth/otp")) {
            return RateLimitType.OTP;
        }

        if (uri.startsWith("/api/v1/auth")) {
            return RateLimitType.AUTH;
        }

        if ("GET".equals(method)) {
            return RateLimitType.READ;
        }

        return RateLimitType.WRITE;
    }

    private String resolveRateLimitKey(HttpServletRequest request, RateLimitType type) {

        // OTP & AUTH → IP-based only
        if (type == RateLimitType.OTP || type == RateLimitType.AUTH) {
            return "rate:" + type.name().toLowerCase() + ":" + resolveClientIp(request);
        }

        // Authenticated → Dealer-based
        Optional<String> dealerId = resolveDealerId();
        return dealerId.map(s -> "rate:" + type.name().toLowerCase() + ":dealer:" + s).orElseGet(() -> "rate:" + type.name().toLowerCase() + ":ip:" + resolveClientIp(request));

        // Fallback → IP
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private Optional<String> resolveDealerId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof DealerDetails dealer) {
            return Optional.of(dealer.getId().toString());
        }
        return Optional.empty();
    }

    private BucketConfiguration resolveConfig(RateLimitType type) {
        return switch (type) {
            case OTP -> RateLimitPolicy.otp();
            case AUTH -> RateLimitPolicy.auth();
            case READ -> RateLimitPolicy.read();
            case WRITE -> RateLimitPolicy.write();
        };
    }
}
