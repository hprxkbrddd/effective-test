package com.example.bankcards.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Schema(description = "Роли пользователя в системе")
public enum Role {
    @Schema(description = "Обычный пользователь")
    USER("ROLE_USER"),
    @Schema(description = "Администратор системы")
    ADMIN("ROLE_ADMIN");

    private final String description;
}
