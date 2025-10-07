package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorDTO(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message
) {

    public ErrorDTO {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    // Конструктор без details
    public ErrorDTO(Integer status, String error, String message) {
        this(LocalDateTime.now(), status, error, message);
    }
}
