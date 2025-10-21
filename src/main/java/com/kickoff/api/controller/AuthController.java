package com.kickoff.api.controller;

import com.kickoff.api.dto.AuthCadastroDTO;
import com.kickoff.api.dto.AuthResponseDTO;
import com.kickoff.api.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
// Esta classe NÃO precisa do @CrossOrigin pois já configuramos globalmente no WebConfig
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody AuthCadastroDTO dto) {
        try {
            AuthResponseDTO response = authService.registrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            // Retorna um erro 400 (Bad Request) se o email já existir ou o tipo for inválido
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Retorna um erro 500 para qualquer outro problema
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao registrar usuário.");
        }
    }
}