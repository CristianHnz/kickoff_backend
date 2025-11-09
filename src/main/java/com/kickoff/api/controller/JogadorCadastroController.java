package com.kickoff.api.controller;

import com.kickoff.api.dto.role.JogadorCadastroDTO;
import com.kickoff.api.dto.role.JogadorResponseDTO;
import com.kickoff.api.mapper.JogadorMapper;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.service.JogadorCadastroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jogadores")
public class JogadorCadastroController {

    @Autowired
    private JogadorCadastroService cadastroService;

    @Autowired
    private JogadorMapper jogadorMapper;

    @PostMapping("/register")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> registrarJogador(@Valid @RequestBody JogadorCadastroDTO dto) {
        try {
            Jogador jogador = cadastroService.registrarJogadorCompleto(dto);
            JogadorResponseDTO resp = jogadorMapper.toJogadorResponseDTO(jogador);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao registrar jogador.");
        }
    }
}
