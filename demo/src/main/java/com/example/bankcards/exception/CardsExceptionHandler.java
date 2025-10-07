package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CardsExceptionHandler {

    @ExceptionHandler({
            IllegalStateException.class,
            CardPropertyNotAccessibleException.class,
            InvalidCardException.class,
            BalanceException.class
    })
    public ResponseEntity<ErrorDTO> handleBadRequestExceptions(
            RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getClass().getName(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler({UsernameNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorDTO> handleNotFoundException(
            RuntimeException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getClass().getName(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler({UnauthorizedException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorDTO> handleUnauthorizedException(
            UnauthorizedException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDTO(
                        HttpStatus.UNAUTHORIZED.value(),
                        ex.getClass().getName(),
                        ex.getMessage()
                ));
    }
}
