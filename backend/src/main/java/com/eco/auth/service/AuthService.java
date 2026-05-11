package com.eco.auth.service;

import com.eco.auth.dto.AuthResponse;
import com.eco.auth.dto.AuthUserResponse;
import com.eco.auth.dto.LoginRequest;
import com.eco.auth.model.RefreshToken;
import com.eco.auth.repository.RefreshTokenRepository;
import com.eco.common.exception.UnauthorizedException;
import com.eco.user.model.User;
import com.eco.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long refreshTokenSeconds;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${eco.auth.refresh-token-seconds}") long refreshTokenSeconds
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenSeconds = refreshTokenSeconds;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciais invalidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Credenciais invalidas");
        }

        return createAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = findValidRefreshToken(refreshTokenValue);
        refreshToken.revoke();

        return createAuthResponse(refreshToken.getUser());
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        String tokenHash = hash(refreshTokenValue);
        refreshTokenRepository.findByTokenHash(tokenHash)
                .filter(refreshToken -> !refreshToken.isRevoked())
                .ifPresent(RefreshToken::revoke);
    }

    public AuthUserResponse me(User user) {
        return AuthUserResponse.fromEntity(user);
    }

    private AuthResponse createAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenValue = UUID.randomUUID() + "." + UUID.randomUUID();

        RefreshToken refreshToken = new RefreshToken(
                user,
                hash(refreshTokenValue),
                Instant.now().plusSeconds(refreshTokenSeconds)
        );
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                refreshTokenValue,
                "Bearer",
                jwtService.getAccessTokenSeconds(),
                AuthUserResponse.fromEntity(user)
        );
    }

    private RefreshToken findValidRefreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hash(refreshTokenValue))
                .orElseThrow(() -> new UnauthorizedException("Refresh token invalido"));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new UnauthorizedException("Refresh token invalido");
        }

        return refreshToken;
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception exception) {
            throw new IllegalStateException("Falha ao gerar hash", exception);
        }
    }
}
