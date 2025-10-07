package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardType;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.BalanceException;
import com.example.bankcards.exception.CardPropertyNotAccessibleException;
import com.example.bankcards.exception.InvalidCardException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.PriorityQueue;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardNumberGenerator cardNumberGenerator;

    private final Queue<Long> cardIdsToBlock = new PriorityQueue<>();

    public Page<CardDTO> getAll(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(Card::toDTOEncrypted);
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

    public Page<CardDTO> getCardsOfUser(String userId, Pageable pageable) {
        if (userRepository.findById(userId).isEmpty())
            throw new EntityNotFoundException("Could not fetch cards of user. User is not in database");
        return cardRepository.findByOwnerId(userId, pageable)
                .map(Card::toDTOEncrypted);
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

    public void addToBlockQueue(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Could not add card to block queue. Card does not exist"));
        switch (card.getStatus()) {
            case ACTIVE -> cardIdsToBlock.add(cardId);
            case BLOCKED -> throw new InvalidCardException("Card is already blocked");
            case EXPIRED -> throw new InvalidCardException("Card is expired and could not be blocked");
        }
    }

    public void blockAllRequested() {
        cardRepository.blockCards(cardIdsToBlock);
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
            throw new InvalidCardException("Invalid card. Could not deposit funds. Card-id:" + cardId + " is blocked or expired.");
        int res = cardRepository.deposit(cardId, amount);
        if (res == 0)
            throw new CardPropertyNotAccessibleException("Could not deposit funds. Card does not exist");
    }

    public void withdraw(Long cardId, BigDecimal amount) {
        if (invalid(cardId))
            throw new InvalidCardException("Invalid card. Card-id:" + cardId + " is blocked or expired");
        int res = cardRepository.withdraw(cardId, amount);
        if (res == 0)
            throw new BalanceException("Could not withdraw funds. Balance is less than withdraw amount or card does not exist");
    }

    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        if (invalid(fromId) || invalid(toId))
            throw new InvalidCardException("Invalid card. Card-id:" + (invalid(fromId) ? fromId : toId) + " is blocked or expired");
        boolean res = cardRepository.transfer(fromId, toId, amount);
        if (!res)
            throw new BalanceException("Could not transfer funds. Balance of Card-id:" + fromId + " is less than withdraw amount or one of cards does not exist");
    }

    private boolean invalid(Long cardId) {
        CardStatus status = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Invalid card. Card does not exist"))
                .getStatus();
        return !status.equals(CardStatus.ACTIVE);
    }

    public CardDTO delete(Long id){
        CardDTO card = cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invalid card. Card does not exist"))
                .toDTO();
        cardRepository.deleteById(id);
        return card;
    }
}
