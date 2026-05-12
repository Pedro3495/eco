package com.eco.auth.controller;

import com.eco.auth.dto.AuthResponse;
import com.eco.auth.dto.AuthUserResponse;
import com.eco.auth.dto.LoginRequest;
import com.eco.auth.dto.LogoutRequest;
import com.eco.auth.dto.RefreshTokenRequest;
import com.eco.auth.service.AuthService;
import com.eco.common.exception.UnauthorizedException;
import com.eco.user.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String ACCESS_TOKEN_COOKIE = "eco_access_token";
    private static final String REFRESH_TOKEN_COOKIE = "eco_refresh_token";

    private final AuthService authService;
    private final boolean cookieSecure;
    private final long refreshTokenSeconds;

    public AuthController(
            AuthService authService,
            @Value("${eco.auth.cookie-secure}") boolean cookieSecure,
            @Value("${eco.auth.refresh-token-seconds}") long refreshTokenSeconds
    ) {
        this.authService = authService;
        this.cookieSecure = cookieSecure;
        this.refreshTokenSeconds = refreshTokenSeconds;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = authService.login(request);
        return withAuthCookies(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody(required = false) RefreshTokenRequest request,
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshTokenCookie
    ) {
        AuthResponse response = authService.refresh(resolveRefreshToken(request, refreshTokenCookie));
        return withAuthCookies(response);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout(
            @RequestBody(required = false) LogoutRequest request,
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshTokenCookie
    ) {
        authService.logout(resolveRefreshToken(request, refreshTokenCookie));
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expiredCookie(ACCESS_TOKEN_COOKIE).toString())
                .header(HttpHeaders.SET_COOKIE, expiredCookie(REFRESH_TOKEN_COOKIE).toString())
                .build();
    }

    @GetMapping("/me")
    public AuthUserResponse me(@AuthenticationPrincipal User user) {
        return authService.me(user);
    }

    private ResponseEntity<AuthResponse> withAuthCookies(AuthResponse response) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookie(ACCESS_TOKEN_COOKIE, response.accessToken(), response.expiresIn()).toString())
                .header(HttpHeaders.SET_COOKIE, authCookie(REFRESH_TOKEN_COOKIE, response.refreshToken(), refreshTokenSeconds).toString())
                .body(response);
    }

    private ResponseCookie authCookie(String name, String value, long maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }

    private ResponseCookie expiredCookie(String name) {
        return authCookie(name, "", 0);
    }

    private String resolveRefreshToken(RefreshTokenRequest request, String refreshTokenCookie) {
        if (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            return request.getRefreshToken();
        }
        if (refreshTokenCookie != null && !refreshTokenCookie.isBlank()) {
            return refreshTokenCookie;
        }
        throw new UnauthorizedException("Refresh token invalido");
    }

    private String resolveRefreshToken(LogoutRequest request, String refreshTokenCookie) {
        if (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            return request.getRefreshToken();
        }
        if (refreshTokenCookie != null && !refreshTokenCookie.isBlank()) {
            return refreshTokenCookie;
        }
        throw new UnauthorizedException("Refresh token invalido");
    }
}
