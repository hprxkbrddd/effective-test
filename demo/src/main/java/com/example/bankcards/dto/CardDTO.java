package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.YearMonth;

@Schema(description = "DTO для представления банковской карты")
public record CardDTO(
        @Schema(description = "Уникальный идентификатор карты", example = "1")
        Long id,

        @Schema(description = "Номер банковской карты", example = "**** **** **** 5678")
        String cardNumber,

        @Schema(description = "Идентификатор владельца карты", example = "user123")
        String ownerId,

        @Schema(description = "Срок действия карты в формате ГГГГ-ММ", example = "2025-12")
        YearMonth expiryDate,

        @Schema(description = "Статус карты", implementation = CardStatus.class)
        CardStatus status,

        @Schema(description = "Баланс карты", example = "1500.75")
        BigDecimal balance
) {
}