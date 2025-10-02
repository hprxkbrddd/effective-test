package com.example.bankcards.entity;

import com.example.bankcards.util.CardNumberEncryptor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@RequiredArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = CardNumberEncryptor.class)
    @Column(name = "card_number", nullable = false, unique = true)
    @Getter
    private final String cardNumber;

    @Column(name = "card_holder", nullable = false)
    @Getter
    private final String ownerId;

    @Column(name = "expiry_date", nullable = false)
    @Getter
    private YearMonth expiryDate;

    @Column(name = "status", nullable = false)
    @Getter
    private CardStatus status;

    @Column(name = "balance", nullable = false)
    @Getter
    private BigDecimal balance = BigDecimal.ZERO;

    public String getCardNumberEncrypted(){
        return "**** **** **** "+cardNumber.substring(15);
    }
}
