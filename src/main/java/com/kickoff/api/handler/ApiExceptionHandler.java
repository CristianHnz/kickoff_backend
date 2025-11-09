// src/main/java/com/kickoff/api/handler/ApiExceptionHandler.java
package com.kickoff.api.handler;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleEnumMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Valor inválido para o status. Use: AGENDADA, CONFIRMADA, EM_ANDAMENTO, FINALIZADA ou CANCELADA.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parâmetro inválido.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos.");
    }
}
