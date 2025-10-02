package com.example.bankcards.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
    USER("Пользователь"),
    ADMIN("Администратор");

    private final String description;
}
