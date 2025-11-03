package com.kickoff.api.controller;

import com.kickoff.api.dto.match.AvaliacaoDTO;
import com.kickoff.api.dto.match.AvaliacaoResponseDTO;
import com.kickoff.api.mapper.AvaliacaoMapper;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.match.Avaliacao;
import com.kickoff.api.service.AvaliacaoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private AvaliacaoMapper avaliacaoMapper;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR_EQUIPE') or hasRole('TECNICO')")
    public ResponseEntity<?> criarAvaliacao(
            @Valid @RequestBody AvaliacaoDTO dto,
            Authentication authentication) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        Pessoa avaliador = usuarioLogado.getPessoa();

        try {
            Avaliacao novaAvaliacao = avaliacaoService.criarAvaliacao(dto, avaliador);
            AvaliacaoResponseDTO responseDTO = avaliacaoMapper.toAvaliacaoResponseDTO(novaAvaliacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listarAvaliacoes(
            @RequestParam(value = "partidaId", required = false) Long partidaId,
            @RequestParam(value = "jogadorId", required = false) Long jogadorId) {

        try {
            List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoes(partidaId, jogadorId);
            List<AvaliacaoResponseDTO> responseDTOs = avaliacaoMapper.toAvaliacaoResponseDTOList(avaliacoes);
            return ResponseEntity.ok(responseDTOs);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarAvaliacaoPorId(@PathVariable Long id) {
        try {
            Avaliacao avaliacao = avaliacaoService.buscarAvaliacaoPorId(id);
            AvaliacaoResponseDTO responseDTO = avaliacaoMapper.toAvaliacaoResponseDTO(avaliacao);
            return ResponseEntity.ok(responseDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE') or hasRole('TECNICO')")
    public ResponseEntity<?> atualizarAvaliacao(
            @PathVariable Long id,
            @Valid @RequestBody AvaliacaoDTO dto,
            Authentication authentication) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        Pessoa avaliador = usuarioLogado.getPessoa();

        try {
            Avaliacao avaliacaoAtualizada = avaliacaoService.atualizarAvaliacao(id, dto, avaliador);
            AvaliacaoResponseDTO responseDTO = avaliacaoMapper.toAvaliacaoResponseDTO(avaliacaoAtualizada);
            return ResponseEntity.ok(responseDTO);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE') or hasRole('TECNICO')")
    public ResponseEntity<?> deletarAvaliacao(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        Pessoa avaliador = usuarioLogado.getPessoa();

        try {
            avaliacaoService.deletarAvaliacao(id, avaliador);
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir avaliação.");
        }
    }
}