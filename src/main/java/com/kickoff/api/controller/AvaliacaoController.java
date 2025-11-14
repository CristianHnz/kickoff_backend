package com.kickoff.api.controller;

import com.kickoff.api.dto.match.AvaliacaoDTO;
import com.kickoff.api.service.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> avaliar(@RequestBody @Valid AvaliacaoDTO dto) {
        avaliacaoService.salvarAvaliacao(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/partida/{partidaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AvaliacaoDTO>> listarDaPartida(@PathVariable Long partidaId) {
        return ResponseEntity.ok(avaliacaoService.listarPorPartida(partidaId));
    }
}