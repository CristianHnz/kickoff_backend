package com.kickoff.api.controller;

import com.kickoff.api.dto.core.EquipeDTO;
import com.kickoff.api.dto.team.EquipeStatsDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.service.EquipeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    @Autowired
    private EquipeService equipeService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EquipeDTO>> listar() {
        return ResponseEntity.ok(equipeService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EquipeDTO> buscarPorId(@PathVariable Long id) {
        Equipe equipe = equipeService.buscarPorId(id);
        return ResponseEntity.ok(new EquipeDTO(
                equipe.getId(),
                equipe.getNome(),
                equipe.getCidade(),
                equipe.getEstado(),
                equipe.getAdministrador().getId(),
                equipe.getEscudo(),
                equipe.getCorPrimaria(),
                equipe.getApelido(),
                equipe.getDataFundacao()
        ));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<EquipeDTO> criar(
            @RequestBody @Valid EquipeDTO dto,
            @AuthenticationPrincipal String email,
            UriComponentsBuilder uriBuilder
    ) {
        Equipe equipeSalva = equipeService.criarEquipe(dto, email);

        URI uri = uriBuilder.path("/api/equipes/{id}").buildAndExpand(equipeSalva.getId()).toUri();
        return ResponseEntity.created(uri).body(new EquipeDTO(
                equipeSalva.getId(), equipeSalva.getNome(), equipeSalva.getCidade(), equipeSalva.getEstado(), equipeSalva.getAdministrador().getId(), equipeSalva.getEscudo(), equipeSalva.getCorPrimaria(), equipeSalva.getApelido(), equipeSalva.getDataFundacao()
        ));
    }

    @GetMapping("/minha-equipe")
    @PreAuthorize("hasRole('GESTOR_EQUIPE') or hasRole('JOGADOR') or hasRole('TECNICO') or hasRole('AUXILIAR')")
    public ResponseEntity<EquipeDTO> buscarMinhaEquipe(
            @AuthenticationPrincipal String email
    ) {
        Equipe equipe = equipeService.buscarMinhaEquipe(email);

        return ResponseEntity.ok(new EquipeDTO(
                equipe.getId(),
                equipe.getNome(),
                equipe.getCidade(),
                equipe.getEstado(),
                equipe.getAdministrador().getId(),
                equipe.getEscudo(),
                equipe.getCorPrimaria(),
                equipe.getApelido(),
                equipe.getDataFundacao()
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<EquipeDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid EquipeDTO dto
    ) {
        Equipe equipeAtualizada = equipeService.atualizar(id, dto);
        return ResponseEntity.ok(new EquipeDTO(
                equipeAtualizada.getId(),
                equipeAtualizada.getNome(),
                equipeAtualizada.getCidade(),
                equipeAtualizada.getEstado(),
                equipeAtualizada.getAdministrador().getId(),
                equipeAtualizada.getEscudo(),
                equipeAtualizada.getCorPrimaria(),
                equipeAtualizada.getApelido(),
                equipeAtualizada.getDataFundacao()
        ));
    }

    @GetMapping("/meus")
    public ResponseEntity<List<EquipeDTO>> listarMeusTimes() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(equipeService.listarMeusTimes(email));
    }

    @GetMapping("/{id}/estatisticas")
    public ResponseEntity<EquipeStatsDTO> getEstatisticas(@PathVariable Long id) {
        return ResponseEntity.ok(equipeService.buscarEstatisticas(id));
    }
}