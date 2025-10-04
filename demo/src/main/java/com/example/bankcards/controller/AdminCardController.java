package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreationDTO;
import com.example.bankcards.dto.CardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/card")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards(){
        return null;
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<CardDTO> getCardById(@RequestParam String cardId){
        return null;
    }

    @PostMapping
    public ResponseEntity<String> createCard(@RequestBody CardCreationDTO dto){
        return null;
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
