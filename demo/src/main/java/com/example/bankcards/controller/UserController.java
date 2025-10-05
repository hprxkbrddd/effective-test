package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.exception.CardPropertyNotAccessibleException;
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

    // TODO pageable
    @GetMapping("/card")
    public ResponseEntity<List<CardDTO>> getUsersCards(
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(cardService.getCardsOfUser(
                    jwtService.extractId(authHeader.substring(7))
            ));
        } else {
            throw new UnauthorizedException("Auth header is either not present or unreadable");
        }
    }

    @PostMapping("/card")
    public ResponseEntity<CardDTO> createCard(
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(cardService.create(
                    jwtService.extractId(authHeader.substring(7))
            ));
        } else {
            throw new UnauthorizedException("Auth header is either not present or unreadable");
        }
    }

    @PutMapping("/card/block")
    public ResponseEntity<String> blockCardRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String cardId) {
        return ResponseEntity.ok("Block request from user-id:" +
                jwtService.extractId(authHeader.substring(7)) +
                " has been sent to admin.\n Card to block: " + cardId);
    }

    @GetMapping("/card/balance")
    public ResponseEntity<BigDecimal> getCardBalance(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long cardId) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(cardService.getBalance(
                    cardId,
                    jwtService.extractId(authHeader.substring(7))
            ));
        } else {
            throw new UnauthorizedException("Auth header is either not present or unreadable");
        }
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody UserCreationDTO dto) {
        return ResponseEntity.ok(userService.getToken(dto.username(), dto.password()));
    }

    @PutMapping("/card/deposit")
    public ResponseEntity<String> deposit(@RequestParam BigDecimal amount, @RequestParam Long cardId) {
        cardService.deposit(cardId, amount);
        return ResponseEntity.ok("Funds have been deposited: +" + amount.toString());
    }

    @PutMapping("/card/withdraw")
    public ResponseEntity<String> withdraw(@RequestParam BigDecimal amount, @RequestParam Long cardId) {
        int res = cardService.withdraw(cardId, amount);
        if (res == 0)
            throw new CardPropertyNotAccessibleException("Could not withdraw funds. Balance is less than withdraw amount");
        else
            return ResponseEntity.ok("Funds have been withdrawn: -" + amount.toString());
    }
}
