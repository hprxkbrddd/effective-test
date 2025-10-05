package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardEncryptedDTO;
import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.JwtService;
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
    private final CardService cardService;
    private final JwtService jwtService;

    @GetMapping("/card")
    public ResponseEntity<List<Card>> getUsersCards(
            @RequestHeader("Authorization") String authHeader){
        return null;
    }

    @PostMapping("/card")
    public ResponseEntity<CardDTO> createCard(
            @RequestHeader("Authorization") String authHeader){
        if (authHeader !=null && authHeader.startsWith("Bearer ")){
            return ResponseEntity.ok(cardService.create(
                    jwtService.extractId(authHeader.substring(7))
            ));
        }else {
            throw new UnauthorizedException("Auth header is either not present or unreadable");
        }
    }

    @PutMapping("/card/block")
    public ResponseEntity<String> blockCardRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String cardId){
        return ResponseEntity.ok("Block request from user-id:"+
                jwtService.extractId(authHeader.substring(7))+
                " has been sent to admin.\n Card to block: "+cardId);
    }

    @GetMapping("/card/balance")
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
