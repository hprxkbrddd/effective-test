package com.example.bankcards.dto;

public record CardEncryptedDTO(
        Long id,
        String encryptedCardNumber
) {
}
