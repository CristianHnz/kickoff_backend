// src/main/java/com/kickoff/api/controller/EquipeController.java
package com.kickoff.api.controller;

import com.kickoff.api.dto.core.EquipeDTO;
import com.kickoff.api.dto.core.EquipeResponseDTO; // IMPORTADO
import com.kickoff.api.mapper.EquipeMapper; // IMPORTADO
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.service.EquipeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    @Autowired
    private EquipeService equipeService;

    @Autowired
    private EquipeMapper equipeMapper;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> criarEquipe(@Valid @RequestBody EquipeDTO dto, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        try {
            Equipe novaEquipe = equipeService.criarEquipe(dto, usuarioLogado);

            EquipeResponseDTO responseDTO = equipeMapper.toEquipeResponseDTO(novaEquipe);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar equipe.");
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<List<EquipeResponseDTO>> listarMinhasEquipes(Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        List<Equipe> equipes = equipeService.buscarEquipesPorAdministrador(usuarioLogado);

        List<EquipeResponseDTO> responseDTOs = equipeMapper.toEquipeResponseDTOList(equipes);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<EquipeResponseDTO> buscarEquipePorId(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        return equipeService.buscarEquipePorIdEAdministrador(id, usuarioLogado)
                .map(equipe -> equipeMapper.toEquipeResponseDTO(equipe)) // Mapeamos a entidade para DTO
                .map(dto -> ResponseEntity.ok(dto))
                .orElse(ResponseEntity.notFound().build());
    }
}