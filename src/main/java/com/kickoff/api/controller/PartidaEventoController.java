package com.kickoff.api.controller;

import com.kickoff.api.dto.match.PartidaEventoDTO;
import com.kickoff.api.dto.match.PartidaEventoResponseDTO;
import com.kickoff.api.mapper.PartidaEventoMapper;
import com.kickoff.api.model.match.PartidaEvento;
import com.kickoff.api.service.PartidaEventoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidas/{partidaId}/eventos")
public class PartidaEventoController {

    @Autowired
    private PartidaEventoService eventoService;

    @Autowired
    private PartidaEventoMapper eventoMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> criarEvento(
            @PathVariable Long partidaId,
            @Valid @RequestBody PartidaEventoDTO dto) {

        try {
            PartidaEvento novoEvento = eventoService.criarEvento(partidaId, dto);
            PartidaEventoResponseDTO responseDTO = eventoMapper.toPartidaEventoResponseDTO(novoEvento);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listarEventos(@PathVariable Long partidaId) {
        try {
            List<PartidaEvento> eventos = eventoService.listarEventosPorPartida(partidaId);
            List<PartidaEventoResponseDTO> responseDTOs = eventoMapper.toPartidaEventoResponseDTOList(eventos);
            return ResponseEntity.ok(responseDTOs);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}