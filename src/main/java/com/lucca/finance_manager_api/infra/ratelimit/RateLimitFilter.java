package com.lucca.finance_manager_api.infra.ratelimit;

import com.lucca.finance_manager_api.config.TokenConfig;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    RateLimitService rateLimitService;

    @Autowired
    TokenConfig tokenConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String key = resolveKey(request);

        Bucket bucket = rateLimitService.resolveBucket(key);

        if (bucket.tryConsume(1)) {
            System.out.println("KEY: " + key);
            System.out.println("Tokens restantes: " + bucket.getAvailableTokens());

            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
        }
    }

    private String resolveKey (HttpServletRequest request) {
        String path = request.getRequestURI();

        if (path.startsWith("/auth")) {
            return "ip: " + request.getRemoteAddr();
        }

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer")) {
            try {
                Long userId = tokenConfig.extractUserId(token.substring(7));
                return "user:" + userId;
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
        return "ip" + request.getRemoteAddr();
    }
}
