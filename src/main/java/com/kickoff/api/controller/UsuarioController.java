package com.kickoff.api.controller;

import com.kickoff.api.dto.auth.AlterarSenhaDTO;
import com.kickoff.api.dto.auth.MeuPerfilDTO;
import com.kickoff.api.service.AuthService;
import com.kickoff.api.service.PerfilService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private AuthService authService;
    @Autowired
    private PerfilService perfilService;

    @PatchMapping("/alterar-senha")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> alterarSenha(
            @AuthenticationPrincipal String email,
            @RequestBody @Valid AlterarSenhaDTO dto
    ) {
        authService.alterarSenha(email, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/meu-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MeuPerfilDTO> getMeuPerfil(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(perfilService.getMeuPerfil(email));
    }

    @PutMapping("/meu-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateMeuPerfil(
            @AuthenticationPrincipal String email,
            @RequestBody @Valid MeuPerfilDTO dto
    ) {
        perfilService.updateMeuPerfil(email, dto);
        return ResponseEntity.ok().build();
    }

}