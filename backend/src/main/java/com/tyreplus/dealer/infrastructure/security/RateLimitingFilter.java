package com.tyreplus.dealer.infrastructure.security;

/*
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
*/
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

    // private final ProxyManager<String> proxyManager;

    public RateLimitingFilter( /* ProxyManager<String> proxyManager */ ) {
        // this.proxyManager = proxyManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Rate Limiting Disabled
        filterChain.doFilter(request, response);
    }
}
