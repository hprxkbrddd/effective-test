package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;

public record UserDTO(
        String id,
        String username,
        Role role
) {
}
