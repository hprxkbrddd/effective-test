package com.example.bankcards.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "Статусы банковской карты")
public enum CardStatus {
    @Schema(description = "Активная карта")
    ACTIVE("Активна"),
    @Schema(description = "Заблокированная карта")
    BLOCKED("Заблокирована"),
    @Schema(description = "Просроченная карта")
    EXPIRED("Истек срок");

    private final String description;
}
