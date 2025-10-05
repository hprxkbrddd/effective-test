package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/card")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards(){
        return ResponseEntity.ok(cardService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getCardById(@PathVariable Long cardId){
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CardDTO> createCardForUser(@RequestParam String ownerId){
        return ResponseEntity.ok(cardService.create(ownerId));
    }

    @PutMapping("/activate")
    public ResponseEntity<String> activateCard(@RequestParam String cardId){
        return null;
    }

    @PutMapping("/block")
    public ResponseEntity<String> blockCard(@RequestParam String cardId){
        return null;
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCard(@RequestParam String cardId) {
        return null;
    }
}
