package com.kickoff.api.controller;

import com.kickoff.api.dto.match.CampeonatoDetalhesDTO;
import com.kickoff.api.dto.match.CampeonatoInputDTO;
import com.kickoff.api.dto.match.CampeonatoResponseDTO;
import com.kickoff.api.dto.match.InscricaoRequestDTO;
import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.service.CampeonatoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/campeonatos")
public class CampeonatoController {

    @Autowired
    private CampeonatoService campeonatoService;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<CampeonatoResponseDTO> criar(
            @RequestBody @Valid CampeonatoInputDTO dto,
            UriComponentsBuilder uriBuilder
    ) {
        Campeonato campeonato = campeonatoService.criarCampeonato(dto);

        URI uri = uriBuilder.path("/api/campeonatos/{id}").buildAndExpand(campeonato.getId()).toUri();

        CampeonatoResponseDTO responseDTO = new CampeonatoResponseDTO(
                campeonato.getId(), campeonato.getNome(), campeonato.getAno(),
                campeonato.getDataInicio(), campeonato.getDataFim(), campeonato.getStatus()
        );

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CampeonatoResponseDTO>> listar() {
        return ResponseEntity.ok(campeonatoService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CampeonatoDetalhesDTO> buscarDetalhes(@PathVariable Long id) {
        return ResponseEntity.ok(campeonatoService.buscarDetalhes(id));
    }

    @PostMapping("/{id}/inscrever")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> inscrever(
            @PathVariable Long id,
            @Valid @RequestBody InscricaoRequestDTO dto
    ) {
        campeonatoService.inscreverEquipe(id, dto.equipeId());
        return ResponseEntity.ok().build();
    }
}