package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;

import java.math.BigDecimal;
import java.time.YearMonth;

public record CardDTO(
        Long id,
        String cardNumber,
        String ownerId,
        YearMonth expiryDate,
        CardStatus status,
        BigDecimal balance
) {
}
