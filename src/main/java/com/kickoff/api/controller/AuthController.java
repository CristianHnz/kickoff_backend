package com.kickoff.api.controller;

import com.kickoff.api.dto.AuthCadastroDTO;
import com.kickoff.api.dto.AuthResponseDTO;
import com.kickoff.api.dto.LoginDTO;
import com.kickoff.api.dto.TokenDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.service.AuthService;
import com.kickoff.api.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody AuthCadastroDTO dto) {
        try {
            AuthResponseDTO response = authService.registrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
            Authentication auth = authenticationManager.authenticate(usernamePassword);
            Usuario usuario = (Usuario) auth.getPrincipal();
            String token = tokenService.generateToken(usuario);
            return ResponseEntity.ok(new TokenDTO(token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }
    }
}