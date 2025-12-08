package com.kickoff.api.controller;

import com.kickoff.api.dto.match.*;
import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.repository.match.CampeonatoRepository;
import com.kickoff.api.service.CampeonatoService;
import jakarta.persistence.EntityNotFoundException;
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
    @Autowired
    private CampeonatoRepository campeonatoRepository;

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
                campeonato.getDataInicio(), campeonato.getDataFim(), campeonato.getStatus(), campeonato.getMinEquipes(), campeonato.getIdaEVolta(), campeonato.getTipoPartida().getId()
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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<CampeonatoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CampeonatoInputDTO dto
    ) {
        Campeonato campeonato = campeonatoService.atualizarCampeonato(id, dto);

        CampeonatoResponseDTO responseDTO = new CampeonatoResponseDTO(
                campeonato.getId(), campeonato.getNome(), campeonato.getAno(),
                campeonato.getDataInicio(), campeonato.getDataFim(), campeonato.getStatus(), campeonato.getMinEquipes(), campeonato.getIdaEVolta(), campeonato.getTipoPartida().getId()
        );
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        campeonatoService.cancelarCampeonato(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CampeonatoResponseDTO> buscarInfoPorId(@PathVariable Long id) {
        Campeonato c = campeonatoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado"));

        CampeonatoResponseDTO responseDTO = new CampeonatoResponseDTO(
                c.getId(), c.getNome(), c.getAno(),
                c.getDataInicio(), c.getDataFim(), c.getStatus(), c.getMinEquipes(), c.getIdaEVolta(), c.getTipoPartida().getId()
        );
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}/artilharia")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ArtilhariaDTO>> getArtilharia(@PathVariable Long id) {
        return ResponseEntity.ok(campeonatoService.buscarArtilharia(id));
    }

    @PostMapping("/{id}/gerar-tabela")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> gerarTabela(@PathVariable Long id) {
        campeonatoService.gerarTabela(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/finalizar")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> finalizar(@PathVariable Long id) {
        campeonatoService.finalizarCampeonato(id);
        return ResponseEntity.ok().build();
    }
}