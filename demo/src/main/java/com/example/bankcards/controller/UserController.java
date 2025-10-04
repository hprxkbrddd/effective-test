package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

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

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody UserCreationDTO dto){
        return ResponseEntity.ok(userService.getToken(dto.username(), dto.password()));
    }
}
