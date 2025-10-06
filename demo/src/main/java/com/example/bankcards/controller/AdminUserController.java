package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/user")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "API для управления пользователями администратором")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final UserService userService;

    @Operation(
            summary = "Получить список всех пользователей",
            description = "Возвращает пагинированный список всех пользователей с возможностью сортировки"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка пользователей",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAll(
            @Parameter(description = "Номер страницы (начинается с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Поле для сортировки", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Направление сортировки (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDirection,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(userService.getAll(pageable));
    }

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает информацию о пользователе по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение пользователя",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(
            @Parameter(description = "ID пользователя", example = "12345")
            @PathVariable String id,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(userService.getByID(id));
    }

    @Operation(
            summary = "Создать нового пользователя",
            description = "Создает нового пользователя с указанными учетными данными"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping
    public ResponseEntity<UserDTO> addUser(
            @Parameter(description = "Данные для создания пользователя")
            @RequestBody UserCreationDTO dto,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(
                userService.createUser(dto.username(), dto.password())
        );
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удален",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "403", description = "Требуется роль 'ADMIN'",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping
    public ResponseEntity<UserDTO> deleteUser(
            @Parameter(description = "ID пользователя для удаления", example = "12345")
            @RequestParam String id,

            @Parameter(description = "JWT токен в формате Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}