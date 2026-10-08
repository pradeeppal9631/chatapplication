package com.chat.chatapplication.jwtsecurity;

import com.chat.chatapplication.chatServices.RedisServices;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtServices jwtServices;
    private final RedisServices redisServices;

    public JwtFilter(JwtServices jwtServices,RedisServices redisServices) {

        this.jwtServices = jwtServices;
        this.redisServices=redisServices;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (redisServices.isTokenBlacklisted(token)) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().write(
                    "Token has been logged out"
            );

            return;
        }


        // =========================
        // JWT VALIDATION
        // =========================

        try {

            String username =
                    jwtServices.extractUsername(token);


            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            null
                    );


            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);


            filterChain.doFilter(
                    request,
                    response
            );

        } catch (Exception e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().write(
                    "Invalid or expired token"
            );
        }
    }
}