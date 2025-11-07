package com.kickoff.api.controller;

import com.kickoff.api.dto.role.ComissaoTecnicaDTO;
import com.kickoff.api.dto.role.ComissaoTecnicaResponseDTO;
import com.kickoff.api.mapper.ComissaoTecnicaMapper;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.role.ComissaoTecnica;
import com.kickoff.api.service.ComissaoTecnicaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // Rota base
public class ComissaoTecnicaController {

    @Autowired
    private ComissaoTecnicaService comissaoTecnicaService;
    @Autowired
    private ComissaoTecnicaMapper mapper;

    @GetMapping("/equipes/{equipeId}/comissao-tecnica")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> listarComissaoTecnica(
            @PathVariable Long equipeId,
            Authentication authentication) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        try {
            List<ComissaoTecnicaResponseDTO> comissao =
                    comissaoTecnicaService.listarComissaoPorEquipe(equipeId, usuarioLogado);
            return ResponseEntity.ok(comissao);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar comissão técnica: " + e.getMessage());
        }
    }

    @PostMapping("/equipes/{equipeId}/comissao-tecnica")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> criarMembroComissao(
            @PathVariable Long equipeId,
            @Valid @RequestBody ComissaoTecnicaDTO dto,
            Authentication authentication) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        try {
            ComissaoTecnica novoMembro =
                    comissaoTecnicaService.criarMembroComissao(equipeId, dto, usuarioLogado);

            ComissaoTecnicaResponseDTO responseDTO = mapper.toResponseDTO(novoMembro);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (EntityNotFoundException e) {
            // Se a equipe ou a pessoa não forem encontradas
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Se a pessoa já estiver em outra comissão
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar membro da comissão.");
        }
    }
}