package com.kickoff.api.controller;

import com.kickoff.api.model.lookup.Posicao;
import com.kickoff.api.repository.lookup.PosicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PosicaoController {

    @Autowired
    private PosicaoRepository posicaoRepository;

    @GetMapping("/posicoes")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<List<Posicao>> listarPosicoes() {
        return ResponseEntity.ok(posicaoRepository.findAll());
    }
}