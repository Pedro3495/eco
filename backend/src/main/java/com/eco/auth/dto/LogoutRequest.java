package com.eco.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequest {

    @NotBlank
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }
}
