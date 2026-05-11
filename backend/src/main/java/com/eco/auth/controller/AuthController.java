package com.eco.auth.controller;

import com.eco.auth.dto.AuthResponse;
import com.eco.auth.dto.AuthUserResponse;
import com.eco.auth.dto.LoginRequest;
import com.eco.auth.dto.LogoutRequest;
import com.eco.auth.dto.RefreshTokenRequest;
import com.eco.auth.service.AuthService;
import com.eco.user.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return authService.refresh(request.getRefreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Valid LogoutRequest request) {
        authService.logout(request.getRefreshToken());
    }

    @GetMapping("/me")
    public AuthUserResponse me(@AuthenticationPrincipal User user) {
        return authService.me(user);
    }
}
