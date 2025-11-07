package com.kickoff.api.controller;

import com.kickoff.api.dto.role.JogadorDTO;
import com.kickoff.api.dto.role.JogadorResponseDTO;
import com.kickoff.api.mapper.JogadorMapper;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.role.JogadorRepository;
import com.kickoff.api.service.JogadorService;
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
@RequestMapping("/api") // Rota base
public class JogadorController {

    @Autowired
    private JogadorService jogadorService;
    @Autowired
    private JogadorMapper jogadorMapper;
    @Autowired
    private JogadorRepository jogadorRepository;

    @PostMapping("/equipes/{equipeId}/jogadores")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> adicionarJogador(
            @PathVariable Long equipeId,
            @Valid @RequestBody JogadorDTO dto,
            Authentication authentication) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        try {
            Jogador novoJogador = jogadorService.adicionarJogador(equipeId, dto, usuarioLogado);
            JogadorResponseDTO responseDTO = jogadorMapper.toJogadorResponseDTO(novoJogador);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao adicionar jogador.");
        }
    }

    @GetMapping("/equipes/{equipeId}/jogadores")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> listarJogadoresDaEquipe(
            @PathVariable Long equipeId,
            Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        try {
            List<Jogador> jogadores = jogadorService.listarJogadoresPorEquipe(equipeId, usuarioLogado);
            List<JogadorResponseDTO> responseDTOs = jogadorMapper.toJogadorResponseDTOList(jogadores);
            return ResponseEntity.ok(responseDTOs);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao listar jogadores.");
        }
    }

    @GetMapping("/jogadores/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> getJogadorDetails(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        try {
            Jogador jogador = jogadorService.getJogadorDetails(id, usuarioLogado);
            JogadorResponseDTO responseDTO = jogadorMapper.toJogadorResponseDTO(jogador);
            return ResponseEntity.ok(responseDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/jogadores/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> updateJogador(
            @PathVariable Long id,
            @Valid @RequestBody JogadorDTO dto,
            Authentication authentication) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        try {
            Jogador jogadorAtualizado = jogadorService.updateJogador(id, dto, usuarioLogado);
            JogadorResponseDTO responseDTO = jogadorMapper.toJogadorResponseDTO(jogadorAtualizado);
            return ResponseEntity.ok(responseDTO);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar jogador.");
        }
    }

    @DeleteMapping("/jogadores/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> deleteJogador(@PathVariable Long id, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        try {
            jogadorService.deleteJogador(id, usuarioLogado);
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir jogador.");
        }
    }

    @GetMapping("/jogadores/sem-equipe")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<List<JogadorResponseDTO>> listarJogadoresSemEquipe() {
        List<Jogador> jogadores = jogadorService.listarJogadoresSemEquipe();
        List<JogadorResponseDTO> responseDTOs = jogadorMapper.toJogadorResponseDTOList(jogadores);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/sem-equipe")
    public List<JogadorResponseDTO> listarSemEquipe() {
        var jogadores = jogadorRepository.findByEquipeIsNull();
        return jogadores.stream()
                .map(j -> new JogadorResponseDTO(
                        j.getId(),
                        j.getPessoa().getId(),
                        j.getPessoa().getNome(),
                        j.getPessoa().getEmail(),
                        j.getNumeroCamisa(),
                        null,
                        j.getPosicoes()
                ))
                .toList();
    }
}