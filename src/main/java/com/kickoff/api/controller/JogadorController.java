package com.kickoff.api.controller;

import com.kickoff.api.dto.role.ContratacaoDTO;
import com.kickoff.api.dto.role.JogadorCadastroDTO;
import com.kickoff.api.dto.role.JogadorResumoDTO;
import com.kickoff.api.service.JogadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipes/{equipeId}/jogadores")
public class JogadorController {

    @Autowired
    private JogadorService jogadorService;

    @PostMapping
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> adicionarJogador(
            @PathVariable Long equipeId,
            @RequestBody @Valid JogadorCadastroDTO dto
    ) {
        jogadorService.adicionarJogadorAEquipe(equipeId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<JogadorResumoDTO>> listarJogadores(@PathVariable Long equipeId) {
        return ResponseEntity.ok(jogadorService.listarJogadoresDaEquipe(equipeId));
    }

    @GetMapping("/disponiveis")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<JogadorResumoDTO>> listarDisponiveis() {
        return ResponseEntity.ok(jogadorService.listarJogadoresDisponiveis());
    }

    @PostMapping("/contratar")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> contratarExistente(
            @PathVariable Long equipeId,
            @RequestBody @Valid ContratacaoDTO dto
    ) {
        jogadorService.contratarJogadorExistente(equipeId, dto);
        return ResponseEntity.ok().build();
    }
}