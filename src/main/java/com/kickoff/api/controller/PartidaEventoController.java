package com.kickoff.api.controller;

import com.kickoff.api.dto.match.PartidaEventoDTO;
import com.kickoff.api.service.PartidaEventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidas/{partidaId}/eventos")
public class PartidaEventoController {

    @Autowired
    private PartidaEventoService eventoService;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> adicionarEvento(
            @PathVariable Long partidaId,
            @RequestBody @Valid PartidaEventoDTO dto
    ) {
        if (!partidaId.equals(dto.partidaId())) {
            throw new IllegalArgumentException("ID da partida na URL difere do corpo da requisição");
        }
        eventoService.registrarEvento(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PartidaEventoDTO>> listarEventos(@PathVariable Long partidaId) {
        return ResponseEntity.ok(eventoService.listarEventos(partidaId));
    }
}