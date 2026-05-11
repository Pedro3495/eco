package com.eco.auth.security;

import com.eco.auth.service.JwtService;
import com.eco.common.exception.UnauthorizedException;
import com.eco.user.model.User;
import com.eco.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            authenticate(authorizationHeader);
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
        }
    }

    private void authenticate(String authorizationHeader) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        String token = authorizationHeader.substring("Bearer ".length());
        UUID userId = jwtService.validateAndExtractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuario nao encontrado"));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
