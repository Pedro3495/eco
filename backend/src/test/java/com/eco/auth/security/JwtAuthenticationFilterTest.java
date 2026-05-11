package com.eco.auth.security;

import com.eco.auth.service.JwtService;
import com.eco.common.exception.UnauthorizedException;
import com.eco.user.model.User;
import com.eco.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateUserWhenBearerTokenIsValid() throws ServletException, IOException {
        UUID userId = UUID.randomUUID();
        User user = new User("Usuario Dev", "dev@eco.com", "hash");
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer access-token");
        when(jwtService.validateAndExtractUserId("access-token")).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isSameAs(user);
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).validateAndExtractUserId("access-token");
    }

    @Test
    void shouldReturnUnauthorizedWhenBearerTokenIsInvalid() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid-token");
        when(jwtService.validateAndExtractUserId("invalid-token"))
                .thenThrow(new UnauthorizedException("Token invalido"));

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
