package com.kickoff.api.controller;

import com.kickoff.api.dto.match.JogadorEscaladoResponseDTO;
import com.kickoff.api.dto.match.PartidaInputDTO;
import com.kickoff.api.dto.match.PartidaResponseDTO;
import com.kickoff.api.dto.match.SalvarEscalacaoDTO;
import com.kickoff.api.service.PartidaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    @Autowired
    private PartidaService partidaService;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> agendar(@RequestBody @Valid PartidaInputDTO dto) {
        partidaService.agendarPartida(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/equipe/{equipeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PartidaResponseDTO>> listarPorEquipe(@PathVariable Long equipeId) {
        return ResponseEntity.ok(partidaService.listarPartidasDoTime(equipeId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestBody String novoStatus
    ) {
        partidaService.atualizarStatus(id, novoStatus);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PartidaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(partidaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid PartidaInputDTO dto
    ) {
        partidaService.atualizarPartida(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        partidaService.cancelarPartida(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{partidaId}/escalacao/{equipeId}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> salvarEscalacao(
            @PathVariable Long partidaId,
            @PathVariable Long equipeId,
            @Valid @RequestBody SalvarEscalacaoDTO dto
    ) {
        partidaService.salvarEscalacao(partidaId, equipeId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{partidaId}/escalacao")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<JogadorEscaladoResponseDTO>> listarEscalados(
            @PathVariable Long partidaId
    ) {
        return ResponseEntity.ok(partidaService.listarEscalados(partidaId));
    }
}