package com.eco.auth.dto;

import com.eco.user.model.User;

import java.util.UUID;

public record AuthUserResponse(
        UUID id,
        String name,
        String email
) {
    public static AuthUserResponse fromEntity(User user) {
        return new AuthUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
