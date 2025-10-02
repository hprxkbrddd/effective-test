package com.example.bankcards.controller;

import com.example.bankcards.entity.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/card/user")
public class CardUserController {

    @GetMapping("/get-cards")
    public ResponseEntity<List<Card>> getUsersCards(
            @RequestHeader("Authorization") String authHeader){
        return null;
    }

    @PostMapping("/block-request")
    public ResponseEntity<String> blockCardRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String cardId){
        return null;
    }

    @GetMapping("/get-card-balance")
    public ResponseEntity<BigDecimal> getCardBalance(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String cardId){
        return null;
    }
}
