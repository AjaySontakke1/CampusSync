package com.campussync.config;

import com.campussync.entity.User;
import com.campussync.enums.Role;
import com.campussync.repository.UserRepository;
import com.campussync.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = jwtService.extractClaims(token);
            String email = claims.getSubject();
            String tokenRole = claims.get("role", String.class);

            if (email == null || email.isBlank()
                    || tokenRole == null || tokenRole.isBlank()) {
                reject(response, "Invalid JWT claims");
                return;
            }

            Role.valueOf(tokenRole);

            User user = userRepository.findByEmail(email)
                    .orElse(null);

            if (user == null
                    || !Boolean.TRUE.equals(user.getActive())
                    || !user.isEmailVerified()) {
                reject(response, "Account is unavailable");
                return;
            }

            // Use the current database role, not the old JWT role.
            String currentRole = user.getRole().name();

            var authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            List.of(new SimpleGrantedAuthority(
                                    "ROLE_" + currentRole)));

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired JWT");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void reject(
            HttpServletResponse response,
            String message) throws IOException {

        SecurityContextHolder.clearContext();
        response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                message);
    }
}
