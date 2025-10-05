package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardType;
import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardNumberEncryptor;
import com.example.bankcards.util.CardNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardNumberGenerator cardNumberGenerator;

    public List<CardDTO> getAll(){
        return cardRepository.findAll()
                .stream().map(Card::toDTO)
                .toList();
    }

    public CardDTO getById(String cardId){
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Could not fetch card by id. Card is not in database"))
                .toDTO();
    }

    public CardDTO create(String ownerId){
        Card card = new Card(
                cardNumberGenerator.generateCardNumber(CardType.RANDOM),
                ownerId
        );
        cardRepository.save(card);
        return card.toDTO();
    }
}
