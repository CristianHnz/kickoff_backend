package com.kickoff.api.controller;

import com.kickoff.api.dto.match.PartidaDTO;
import com.kickoff.api.dto.match.PartidaResponseDTO;
import com.kickoff.api.mapper.PartidaMapper;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.service.PartidaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    @Autowired
    private PartidaService partidaService;

    @Autowired
    private PartidaMapper partidaMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> criarPartida(@Valid @RequestBody PartidaDTO dto) {
        try {
            Partida novaPartida = partidaService.criarPartida(dto);
            PartidaResponseDTO responseDTO = partidaMapper.toPartidaResponseDTO(novaPartida);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listarPartidas(
            @RequestParam(value = "campeonatoId", required = false) Long campeonatoId) {

        try {
            List<Partida> partidas = partidaService.listarPartidas(campeonatoId);
            List<PartidaResponseDTO> responseDTOs = partidaMapper.toPartidaResponseDTOList(partidas);
            return ResponseEntity.ok(responseDTOs);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarPartidaPorId(@PathVariable Long id) {
        try {
            Partida partida = partidaService.buscarPartidaPorId(id);
            PartidaResponseDTO responseDTO = partidaMapper.toPartidaResponseDTO(partida);
            return ResponseEntity.ok(responseDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> atualizarPartida(@PathVariable Long id, @Valid @RequestBody PartidaDTO dto) {
        try {
            Partida partidaAtualizada = partidaService.atualizarPartida(id, dto);
            PartidaResponseDTO responseDTO = partidaMapper.toPartidaResponseDTO(partidaAtualizada);
            return ResponseEntity.ok(responseDTO);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletarPartida(@PathVariable Long id) {
        try {
            partidaService.deletarPartida(id);
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não é possível excluir a partida. Ela já está associada a eventos ou avaliações.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir partida.");
        }
    }
}