package com.eco.auth.controller;

import com.eco.auth.dto.AuthResponse;
import com.eco.auth.dto.AuthUserResponse;
import com.eco.auth.service.JwtService;
import com.eco.auth.service.AuthService;
import com.eco.user.model.User;
import com.eco.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void loginShouldReturnAuthResponse() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthResponse response = new AuthResponse(
                "access-token",
                "refresh-token",
                "Bearer",
                900L,
                new AuthUserResponse(userId, "Usuario Dev", "dev@eco.com")
        );

        when(authService.login(argThat(request ->
                request.getEmail().equals("dev@eco.com")
                        && request.getPassword().equals("123456")
        ))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                                {
                                  "email": "dev@eco.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andExpect(jsonPath("$.user.id").value(userId.toString()))
                .andExpect(jsonPath("$.user.name").value("Usuario Dev"))
                .andExpect(jsonPath("$.user.email").value("dev@eco.com"));
    }

    @Test
    void meShouldReturnAuthenticatedUser() throws Exception {
        User user = new User("Usuario Dev", "dev@eco.com", "hash");
        AuthUserResponse response = AuthUserResponse.fromEntity(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of()
        );

        when(authService.me(any(User.class))).thenReturn(response);

        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            mockMvc.perform(get("/auth/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId().toString()))
                    .andExpect(jsonPath("$.name").value("Usuario Dev"))
                    .andExpect(jsonPath("$.email").value("dev@eco.com"));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
