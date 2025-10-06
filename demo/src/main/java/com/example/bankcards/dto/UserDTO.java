package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "DTO для представления пользователя")
public record UserDTO(
        @Schema(description = "Уникальный идентификатор пользователя", example = "user-12345")
        String id,

        @Schema(description = "Имя пользователя (логин)", example = "john_doe")
        String username,

        @Schema(description = "Набор ролей пользователя")
        Set<Role> roles
) {
}
