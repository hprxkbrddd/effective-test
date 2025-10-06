package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin Card Management", description = "API для управления банковскими картами администратором")
public class AdminCardController {

    private final CardService cardService;

    @Operation(summary = "Получить все карты", description = "Возвращает пагинированный список всех карт")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка карт",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping
    public ResponseEntity<Page<CardDTO>> getAllCards(
            @Parameter(description = "Номер страницы (по умолчанию 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы (по умолчанию 10)")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Поле для сортировки (по умолчанию id)")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Направление сортировки: asc или desc (по умолчанию asc)")
            @RequestParam(defaultValue = "asc") String sortDirection,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(cardService.getAll(pageable));
    }

    @Operation(summary = "Получить карту по ID", description = "Возвращает карту по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardDTO.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{cardId}")
    public ResponseEntity<CardDTO> getCardById(
            @Parameter(description = "ID карты")
            @PathVariable Long cardId,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(cardService.getById(cardId));
    }

    @Operation(summary = "Получить карту по номеру", description = "Возвращает карту по её номеру")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardDTO.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/number/{cardNumber}")
    public ResponseEntity<CardDTO> getCardByNumber(
            @Parameter(description = "Номер карты")
            @PathVariable String cardNumber,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(cardService.getByNumber(cardNumber));
    }

    @Operation(summary = "Получить карты пользователя", description = "Возвращает пагинированный список карт конкретного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка карт пользователя",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/user")
    public ResponseEntity<Page<CardDTO>> getCardsOfUser(
            @Parameter(description = "Номер страницы (по умолчанию 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы (по умолчанию 10)")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Поле для сортировки (по умолчанию id)")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Направление сортировки: asc или desc (по умолчанию asc)")
            @RequestParam(defaultValue = "asc") String sortDirection,

            @Parameter(description = "ID владельца карты", required = true)
            @RequestParam String ownerId,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(cardService.getCardsOfUser(ownerId, pageable));
    }

    @Operation(summary = "Создать карту для пользователя", description = "Создает новую карту для указанного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardDTO.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping
    public ResponseEntity<CardDTO> createCardForUser(
            @Parameter(description = "ID владельца карты", required = true)
            @RequestParam String ownerId,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(cardService.create(ownerId));
    }

    @Operation(summary = "Активировать карту", description = "Активирует карту по её ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно активирована",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardDTO.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PutMapping("/activate")
    public ResponseEntity<CardDTO> activateCard(
            @Parameter(description = "ID карты для активации", required = true)
            @RequestParam Long cardId,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(
                cardService.setCardStatus(cardId, CardStatus.ACTIVE)
        );
    }

    @Operation(summary = "Заблокировать карту", description = "Блокирует карту по её ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно заблокирована",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardDTO.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PutMapping("/block")
    public ResponseEntity<CardDTO> blockCard(
            @Parameter(description = "ID карты для блокировки", required = true)
            @RequestParam Long cardId,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(
                cardService.setCardStatus(cardId, CardStatus.BLOCKED)
        );
    }

    @Operation(summary = "Заблокировать запрошенные карты", description = "Блокирует все карты, помеченные для блокировки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Все запрошенные карты заблокированы",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PutMapping("/block-requested")
    public ResponseEntity<String> blockRequestedCard(
            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        cardService.blockAllRequested();
        return ResponseEntity.ok("All requested card are blocked");
    }

    @Operation(summary = "Пометить карты как просроченные", description = "Помечает все устаревшие карты как просроченные")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Все устаревшие карты помечены как просроченные",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PutMapping("/expire")
    public ResponseEntity<String> expireCards(
            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        cardService.expire();
        return ResponseEntity.ok("All outdated cards are marked as expired");
    }

    @Operation(summary = "Удалить карту", description = "Удаляет карту по её ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно удалена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping
    public ResponseEntity<String> deleteCard(
            @Parameter(description = "ID карты для удаления", required = true)
            @RequestParam String cardId,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        return null;
    }
}