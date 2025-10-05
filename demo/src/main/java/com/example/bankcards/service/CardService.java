package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardType;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardPropertyNotAccessibleException;
import com.example.bankcards.exception.InvalidCardException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardNumberGenerator cardNumberGenerator;

    public List<Card> getAll() {
        return cardRepository.findAll();
    }

    public CardDTO getById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Could not fetch card by id. Card is not in database"))
                .toDTO();
    }

    public CardDTO getByNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new EntityNotFoundException("Could not fetch card by card number. Card is not in database"))
                .toDTO();
    }

    public List<CardDTO> getCardsOfUser(String userId) {
        if (userRepository.findById(userId).isEmpty())
            throw new EntityNotFoundException("Could not fetch cards of user. User is not in database");
        return cardRepository.findByOwnerId(userId)
                .stream().map(Card::toDTO)
                .toList();
    }

    public CardDTO create(String ownerId) {
        Card card = new Card(
                cardNumberGenerator.generateCardNumber(CardType.RANDOM),
                ownerId
        );
        cardRepository.save(card);
        return card.toDTO();
    }

//    public CardDTO createOutdated(String ownerId){
//        Card card = new Card(
//                cardNumberGenerator.generateCardNumber(CardType.RANDOM),
//                ownerId,
//                CardStatus.EXPIRED
//        );
//        cardRepository.save(card);
//        return card.toDTO();
//    }

    public CardDTO setCardStatus(Long cardId, CardStatus status) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Could not update card status. Card is not in database"));
        card.setStatus(status);
        return card.toDTOEncrypted();
    }

    public void expire() {
        cardRepository.expireCards();
    }

    public BigDecimal getBalance(Long cardId, String ownerId) {
        return cardRepository.findByIdAndOwnerId(cardId, ownerId)
                .orElseThrow(() -> new CardPropertyNotAccessibleException("Could not retrieve balance. Card does not belong to user or does not exist"))
                .getBalance();
    }

    public void deposit(Long cardId, BigDecimal amount) {
        if (invalid(cardId))
            throw new InvalidCardException("Invalid card. Card is blocked or expired");
        int res = cardRepository.deposit(cardId, amount);
        if (res == 0)
            throw new CardPropertyNotAccessibleException("Could not deposit funds. Card does not exist");
    }

    public int withdraw(Long cardId, BigDecimal amount) {
        if (invalid(cardId))
            throw new InvalidCardException("Invalid card. Card is blocked or expired");
        else
            return cardRepository.withdraw(cardId, amount);
    }

    private boolean invalid(Long cardId){
        CardStatus status = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Invalid card. Card does not exist"))
                .getStatus();
        return !status.equals(CardStatus.ACTIVE);
    }
}
