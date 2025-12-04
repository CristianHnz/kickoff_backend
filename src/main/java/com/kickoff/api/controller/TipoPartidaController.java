package com.kickoff.api.controller;

import com.kickoff.api.model.lookup.TipoPartida;
import com.kickoff.api.repository.lookup.TipoPartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-partida")
public class TipoPartidaController {

    @Autowired
    private TipoPartidaRepository tipoPartidaRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TipoPartida>> listarTipos() {
        return ResponseEntity.ok(tipoPartidaRepository.findAll());
    }
}