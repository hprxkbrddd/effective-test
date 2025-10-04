package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;

import java.util.Set;

public record UserDTO(
        String id,
        String username,
        Set<Role> roles
) {
}
