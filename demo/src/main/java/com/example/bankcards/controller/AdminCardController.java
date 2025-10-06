package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/card")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Page<CardDTO>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(cardService.getAll(pageable));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardDTO> getCardById(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.getById(cardId));
    }

    @GetMapping("/number/{cardId}")
    public ResponseEntity<CardDTO> getCardByNumber(@PathVariable String cardNumber) {
        return ResponseEntity.ok(cardService.getByNumber(cardNumber));
    }

    @GetMapping("/user")
    public ResponseEntity<Page<CardDTO>> getCardsOfUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam String ownerId) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(cardService.getCardsOfUser(ownerId, pageable));
    }

    @PostMapping
    public ResponseEntity<CardDTO> createCardForUser(@RequestParam String ownerId) {
        return ResponseEntity.ok(cardService.create(ownerId));
    }

//    @PostMapping("/expired")
//    public ResponseEntity<CardDTO> createExpiredCardForUser(@RequestParam String ownerId){
//        return ResponseEntity.ok(cardService.createOutdated(ownerId));
//    }

    @PutMapping("/activate")
    public ResponseEntity<CardDTO> activateCard(@RequestParam Long cardId) {
        return ResponseEntity.ok(
                cardService.setCardStatus(cardId, CardStatus.ACTIVE)
        );
    }

    @PutMapping("/block")
    public ResponseEntity<CardDTO> blockCard(@RequestParam Long cardId) {
        return ResponseEntity.ok(
                cardService.setCardStatus(cardId, CardStatus.BLOCKED)
        );
    }

    @PutMapping("/block-requested")
    public ResponseEntity<String> blockRequestedCard() {
        cardService.blockAllRequested();
        return ResponseEntity.ok("All requested card are blocked");
    }

    @PutMapping("/expire")
    public ResponseEntity<String> expireCards() {
        cardService.expire();
        return ResponseEntity.ok("All outdated cards are marked as expired");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCard(@RequestParam String cardId) {
        return null;
    }
}
