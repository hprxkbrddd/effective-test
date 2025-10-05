package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardEncryptedDTO;
import com.example.bankcards.dto.CardType;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardNumberEncryptor;
import com.example.bankcards.util.CardNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardNumberGenerator cardNumberGenerator;

    public List<Card> getAll(){
        return cardRepository.findAll();
    }

    public CardDTO getById(Long cardId){
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Could not fetch card by id. Card is not in database"))
                .toDTO();
    }

    public CardDTO getByNumber(String cardNumber){
        return cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new EntityNotFoundException("Could not fetch card by card number. Card is not in database"))
                .toDTO();
    }

    public List<CardDTO> getCardsOfUser(String userId){
        if(userRepository.findById(userId).isEmpty())
            throw new EntityNotFoundException("Could not fetch cards of user. User is not in database");
        return cardRepository.findByOwnerId(userId)
                .stream().map(Card::toDTO)
                .toList();
    }

    public CardDTO create(String ownerId){
        Card card = new Card(
                cardNumberGenerator.generateCardNumber(CardType.RANDOM),
                ownerId
        );
        cardRepository.save(card);
        return card.toDTO();
    }

    public CardDTO setCardStatus(Long cardId, CardStatus status){
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Could not update card status. Card is not in database"));
        card.setStatus(status);
        return card.toDTOEncrypted();
    }

    public void expire(){
        List<Card> expiredCards = cardRepository.findByExpiryDateAfter(YearMonth.now())
                .orElseGet(Collections::emptyList);
        if (expiredCards.isEmpty()) return;
        expiredCards.forEach(card -> card.setStatus(CardStatus.EXPIRED));
    }
}
