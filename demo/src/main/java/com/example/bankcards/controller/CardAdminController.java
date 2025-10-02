package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreationDTO;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/card/admin")
public class CardAdminController {

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/get-all")
    public ResponseEntity<List<CardDTO>> getAllCards(){
        return null;
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/get-by-id")
    public ResponseEntity<CardDTO> getCardById(@RequestParam String cardId){
        return null;
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/create")
    public ResponseEntity<String> createCard(@RequestBody CardCreationDTO dto){
        return null;
    }
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/activate")
    public ResponseEntity<String> activateCard(@RequestParam String cardId){
        return null;
    }
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/block")
    public ResponseEntity<String> blockCard(@RequestParam String cardId){
        return null;
    }
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCard(@RequestParam String cardId) {
        return null;
    }
}
