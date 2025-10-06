package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.JwtService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final CardService cardService;
    private final JwtService jwtService;

    @GetMapping("/card")
    public ResponseEntity<Page<CardDTO>> getUsersCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestHeader("Authorization") String authHeader) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(cardService.getCardsOfUser(
                    jwtService.extractId(authHeader.substring(7)),
                    pageable
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
            @RequestParam Long cardId) {
        cardService.addToBlockQueue(cardId);
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
        cardService.withdraw(cardId, amount);
        return ResponseEntity.ok("Funds have been withdrawn: -" + amount.toString());
    }

    @PutMapping("/card/transfer")
    public ResponseEntity<String> transfer(
            @RequestParam Long fromId,
            @RequestParam Long toId,
            @RequestParam BigDecimal amount
    ) {
        cardService.transfer(fromId, toId, amount);
        return ResponseEntity.ok("Funds have been transferred\nCard-id:" + fromId + " -" + amount + "\nCard-id:" + toId + " +" + amount);
    }
}
