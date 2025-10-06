package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.JwtService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Controller", description = "API для управления картами пользователя и операциями с ними")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final CardService cardService;
    private final JwtService jwtService;

    @Operation(
            summary = "Получить карты пользователя",
            description = "Возвращает пагинированный список карт текущего пользователя с возможностью сортировки"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка карт"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content)
    })
    @GetMapping("/card")
    public ResponseEntity<Page<CardDTO>> getUsersCards(
            @Parameter(description = "Номер страницы (начинается с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Поле для сортировки", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Направление сортировки (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDirection,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
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

    @Operation(
            summary = "Создать новую карту",
            description = "Создает новую банковскую карту для текущего пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно создана"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content)
    })
    @PostMapping("/card")
    public ResponseEntity<CardDTO> createCard(
            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(cardService.create(
                    jwtService.extractId(authHeader.substring(7))
            ));
        } else {
            throw new UnauthorizedException("Auth header is either not present or unreadable");
        }
    }

    @Operation(
            summary = "Заблокировать карту",
            description = "Отправляет запрос на блокировку карты администратору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос на блокировку отправлен"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content)
    })
    @PutMapping("/card/block")
    public ResponseEntity<String> blockCardRequest(
            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader,

            @Parameter(description = "ID карты для блокировки", required = true, example = "123")
            @RequestParam Long cardId) {
        cardService.addToBlockQueue(cardId);
        return ResponseEntity.ok("Block request from user-id:" +
                jwtService.extractId(authHeader.substring(7)) +
                " has been sent to admin.\n Card to block: " + cardId);
    }

    @Operation(
            summary = "Получить баланс карты",
            description = "Возвращает текущий баланс указанной карты пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс успешно получен"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content)
    })
    @GetMapping("/card/balance")
    public ResponseEntity<BigDecimal> getCardBalance(
            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader,

            @Parameter(description = "ID карты", required = true, example = "123")
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

    @Operation(
            summary = "Получить JWT токен",
            description = "Аутентификация пользователя и получение JWT токена для доступа к API",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен успешно получен"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные", content = @Content)
    })
    @PostMapping("/token")
    public ResponseEntity<String> getToken(
            @Parameter(description = "Данные пользователя для аутентификации", required = true)
            @RequestBody UserCreationDTO dto) {
        return ResponseEntity.ok(userService.getToken(dto.username(), dto.password()));
    }

    @Operation(
            summary = "Пополнить карту",
            description = "Пополнение баланса указанной карты"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно пополнена"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content)
    })
    @PutMapping("/card/deposit")
    public ResponseEntity<String> deposit(
            @Parameter(description = "Сумма для пополнения", required = true, example = "1000.00")
            @RequestParam BigDecimal amount,

            @Parameter(description = "ID карты для пополнения", required = true, example = "123")
            @RequestParam Long cardId,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        cardService.deposit(cardId, amount);
        return ResponseEntity.ok("Funds have been deposited: +" + amount.toString());
    }

    @Operation(
            summary = "Снять средства с карты",
            description = "Снятие средств с указанной карты"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Средства успешно сняты"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content)
    })
    @PutMapping("/card/withdraw")
    public ResponseEntity<String> withdraw(
            @Parameter(description = "Сумма для снятия", required = true, example = "500.00")
            @RequestParam BigDecimal amount,

            @Parameter(description = "ID карты для снятия", required = true, example = "123")
            @RequestParam Long cardId,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        cardService.withdraw(cardId, amount);
        return ResponseEntity.ok("Funds have been withdrawn: -" + amount.toString());
    }

    @Operation(
            summary = "Перевести средства между картами",
            description = "Перевод средств с одной карты на другую"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content)
    })
    @PutMapping("/card/transfer")
    public ResponseEntity<String> transfer(
            @Parameter(description = "ID карты отправителя", required = true, example = "123")
            @RequestParam Long fromId,

            @Parameter(description = "ID карты получателя", required = true, example = "456")
            @RequestParam Long toId,

            @Parameter(description = "Сумма перевода", required = true, example = "300.00")
            @RequestParam BigDecimal amount,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader
    ) {
        cardService.transfer(fromId, toId, amount);
        return ResponseEntity.ok("Funds have been transferred\nCard-id:" + fromId + " -" + amount + "\nCard-id:" + toId + " +" + amount);
    }
}