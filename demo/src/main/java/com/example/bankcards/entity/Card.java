package com.example.bankcards.entity;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.util.CardExpiryDateConverter;
import com.example.bankcards.util.CardNumberEncryptor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@NoArgsConstructor
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
        this.status = CardStatus.ACTIVE;
        this.balance = BigDecimal.ZERO;
    }

//    public Card(
//            String cardNumber,
//            String ownerId, CardStatus status
//    ) {
//        this.cardNumber = cardNumber;
//        this.ownerId = ownerId;
//        this.expiryDate = YearMonth.now().minusYears(5);
//        this.status= CardStatus.ACTIVE;
//        this.balance = BigDecimal.ZERO;
//    }

    @Convert(converter = CardNumberEncryptor.class)
    @Column(name = "card_number", nullable = false, unique = true)
    @Getter
    private String cardNumber;

    @Column(name = "card_holder", nullable = false)
    @Getter
    private String ownerId;

    @Column(name = "expiry_date", nullable = false)
    @Convert(converter = CardExpiryDateConverter.class)
    @Getter
    private YearMonth expiryDate;

    @Column(name = "status", nullable = false)
    @Getter
    @Setter
    private CardStatus status;

    @Column(name = "balance", nullable = false)
    @Getter
    private BigDecimal balance;

    public String getCardNumberEncrypted() {
        return "**** **** **** " + cardNumber.substring(12);
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

    public CardDTO toDTOEncrypted() {
        return new CardDTO(
                id,
                getCardNumberEncrypted(),
                ownerId,
                expiryDate,
                status,
                balance
        );
    }
}
