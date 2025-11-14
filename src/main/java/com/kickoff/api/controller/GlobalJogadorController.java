package com.kickoff.api.controller;

import com.kickoff.api.dto.role.JogadorDetalhesDTO;
import com.kickoff.api.dto.role.JogadorResumoDTO;
import com.kickoff.api.service.JogadorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jogadores")
public class GlobalJogadorController {

    @Autowired
    private JogadorService jogadorService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<JogadorResumoDTO>> listarTodosJogadores() {
        return ResponseEntity.ok(jogadorService.listarTodosJogadores());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<JogadorDetalhesDTO> buscarDetalhes(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jogadorService.buscarDetalhesJogador(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}