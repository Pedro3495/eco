package com.eco.auth.service;

import com.eco.auth.dto.AuthResponse;
import com.eco.auth.dto.LoginRequest;
import com.eco.auth.model.RefreshToken;
import com.eco.auth.repository.RefreshTokenRepository;
import com.eco.common.exception.UnauthorizedException;
import com.eco.user.model.User;
import com.eco.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Test
    void loginShouldReturnTokensWhenCredentialsAreValid() {
        User user = new User("Usuario Dev", "dev@eco.com", "hash");
        LoginRequest request = loginRequest("dev@eco.com", "123456");
        AuthService authService = authService();

        when(userRepository.findByEmailIgnoreCase("dev@eco.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "hash")).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.getAccessTokenSeconds()).thenReturn(900L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900L);
        assertThat(response.user().email()).isEqualTo("dev@eco.com");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void loginShouldThrowUnauthorizedWhenPasswordIsInvalid() {
        User user = new User("Usuario Dev", "dev@eco.com", "hash");
        LoginRequest request = loginRequest("dev@eco.com", "wrong-password");
        AuthService authService = authService();

        when(userRepository.findByEmailIgnoreCase("dev@eco.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Credenciais invalidas");

        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void refreshShouldRevokeCurrentRefreshTokenAndReturnNewTokens() {
        User user = new User("Usuario Dev", "dev@eco.com", "hash");
        RefreshToken refreshToken = new RefreshToken(user, hash("refresh-token"), Instant.now().plusSeconds(3600));
        AuthService authService = authService();

        when(refreshTokenRepository.findByTokenHash(hash("refresh-token"))).thenReturn(Optional.of(refreshToken));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtService.getAccessTokenSeconds()).thenReturn(900L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.refresh("refresh-token");

        assertThat(refreshToken.isRevoked()).isTrue();
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isNotBlank();
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void logoutShouldRevokeRefreshTokenWhenItExists() {
        User user = new User("Usuario Dev", "dev@eco.com", "hash");
        RefreshToken refreshToken = new RefreshToken(user, hash("refresh-token"), Instant.now().plusSeconds(3600));
        AuthService authService = authService();

        when(refreshTokenRepository.findByTokenHash(hash("refresh-token"))).thenReturn(Optional.of(refreshToken));

        authService.logout("refresh-token");

        assertThat(refreshToken.isRevoked()).isTrue();
    }

    @Test
    void logoutShouldDoNothingWhenRefreshTokenDoesNotExist() {
        AuthService authService = authService();

        when(refreshTokenRepository.findByTokenHash(hash("missing-token"))).thenReturn(Optional.empty());

        authService.logout("missing-token");

        verify(refreshTokenRepository).findByTokenHash(hash("missing-token"));
    }

    private AuthService authService() {
        return new AuthService(
                userRepository,
                refreshTokenRepository,
                passwordEncoder,
                jwtService,
                604800L
        );
    }

    private LoginRequest loginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "password", password);
        return request;
    }

    private String hash(String value) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
