package com.lucca.finance_manager_api.security;

import com.lucca.finance_manager_api.config.TokenConfig;
import com.lucca.finance_manager_api.dto.JWTUserData;
import com.lucca.finance_manager_api.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenConfig tokenConfig;

    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (Strings.isNotEmpty(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length());
            Optional<JWTUserData> jwtUserData = tokenConfig.validateToken(token);
            if (jwtUserData.isPresent()) {
                JWTUserData userData = jwtUserData.get();

                var user = userRepository.findById(userData.userId());

                if (user.isPresent()) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.get(), null, user.get().getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        }
        else {
            filterChain.doFilter(request, response);
        }
    }

}
