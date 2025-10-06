package com.example.bankcards.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Bad Request - Ошибка в запросе",
                content = @Content(mediaType = "text/plain",
                        examples = @ExampleObject(value = "Invalid card data"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Не авторизован",
                content = @Content(mediaType = "text/plain",
                        examples = @ExampleObject(value = "Access denied"))),
        @ApiResponse(responseCode = "404", description = "Not Found - Ресурс не найден",
                content = @Content(mediaType = "text/plain",
                        examples = @ExampleObject(value = "User not found")))
})
public class CardsExceptionHandler {

    @ExceptionHandler({
            IllegalStateException.class,
            CardPropertyNotAccessibleException.class,
            InvalidCardException.class,
            BalanceException.class
    })
    @ApiResponse(responseCode = "400", description = "Bad Request - Ошибка в запросе",
            content = @Content(mediaType = "text/plain",
                    examples = {
                            @ExampleObject(name = "IllegalState", value = "Invalid operation state"),
                            @ExampleObject(name = "CardProperty", value = "Card property not accessible"),
                            @ExampleObject(name = "InvalidCard", value = "Invalid card data provided"),
                            @ExampleObject(name = "Balance", value = "Insufficient balance")
                    }))
    public ResponseEntity<String> handleBadRequestExceptions(
            RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler({UsernameNotFoundException.class, EntityNotFoundException.class})
    @ApiResponse(responseCode = "404", description = "Not Found - Ресурс не найден",
            content = @Content(mediaType = "text/plain",
                    examples = {
                            @ExampleObject(name = "UserNotFound", value = "User not found"),
                            @ExampleObject(name = "EntityNotFound", value = "Entity not found")
                    }))
    public ResponseEntity<String> handleUsernameNotFoundException(
            RuntimeException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ApiResponse(responseCode = "401", description = "Unauthorized - Не авторизован",
            content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Authentication required")))
    public ResponseEntity<String> handleUnauthorizedException(
            UnauthorizedException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }
}