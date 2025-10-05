package com.example.bankcards.entity;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardEncryptedDTO;
import com.example.bankcards.util.CardNumberEncryptor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Card(
            String cardNumber,
            String ownerId
    ) {
        this.cardNumber = cardNumber;
        this.ownerId = ownerId;
        this.expiryDate = YearMonth.now().plusYears(5);
        this.status= CardStatus.ACTIVE;
        this.balance = BigDecimal.ZERO;
    }

    @Convert(converter = CardNumberEncryptor.class)
    @Column(name = "card_number", nullable = false, unique = true)
    @Getter
    private final String cardNumber;

    @Column(name = "card_holder", nullable = false)
    @Getter
    private final String ownerId;

    @Column(name = "expiry_date", nullable = false)
    @Getter
    private final YearMonth expiryDate;

    @Column(name = "status", nullable = false)
    @Getter
    private CardStatus status;

    @Column(name = "balance", nullable = false)
    @Getter
    private BigDecimal balance;

    public String getCardNumberEncrypted() {
        return "**** **** **** " + cardNumber.substring(15);
    }

    public CardEncryptedDTO toDTOEncrypted() {
        return new CardEncryptedDTO(
                id,
                getCardNumberEncrypted()
        );
    }

    public CardDTO toDTO() {
        return new CardDTO(
                id,
                cardNumber,
                ownerId,
                expiryDate,
                status,
                balance
        );
    }
}
