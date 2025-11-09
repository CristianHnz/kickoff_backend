package com.kickoff.api.controller;

import com.kickoff.api.dto.core.EquipeDTO;
import com.kickoff.api.dto.core.EquipeResponseDTO;
import com.kickoff.api.dto.role.VincularJogadorExistenteRequest;
import com.kickoff.api.mapper.EquipeMapper;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.service.EquipeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> atualizarEquipe(@PathVariable Long id, @Valid @RequestBody EquipeDTO dto, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        try {
            Equipe equipeAtualizada = equipeService.atualizarEquipe(id, dto, usuarioLogado);
            EquipeResponseDTO responseDTO = equipeMapper.toEquipeResponseDTO(equipeAtualizada);
            return ResponseEntity.ok(responseDTO); // Retorna 200 OK com a equipe atualizada

        } catch (EntityNotFoundException e) {
            // Se o serviço não encontrar a equipe (ou não pertencer ao usuário)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalArgumentException e) {
            // Se o nome da equipe já existir
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar equipe.");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> deletarEquipe(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        try {
            equipeService.deletarEquipe(id, usuarioLogado);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não é possível excluir a equipe. Ela já está associada a jogadores ou partidas.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir equipe.");
        }
    }

    @PostMapping("/{id}/jogadores/existente")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> vincularJogadorSemEquipe(
            @PathVariable Long id,
            @RequestBody @Valid VincularJogadorExistenteRequest body,
            Authentication authentication
    ) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        try {
            equipeService.vincularJogadorSemEquipe(id, body.jogadorId(), usuarioLogado);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao vincular jogador à equipe.");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EquipeResponseDTO>> listarTodasEquipes() {
        var equipes = equipeService.listarTodas();
        var dtos = equipeMapper.toEquipeResponseDTOList(equipes);
        return ResponseEntity.ok(dtos);
    }

}