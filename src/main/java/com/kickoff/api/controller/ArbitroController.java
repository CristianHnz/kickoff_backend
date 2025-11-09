package com.kickoff.api.controller;

import com.kickoff.api.dto.role.ArbitroListDTO;
import com.kickoff.api.model.role.Arbitro;
import com.kickoff.api.repository.role.ArbitroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arbitros")
public class ArbitroController {

    @Autowired
    private ArbitroRepository arbitroRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ArbitroListDTO>> listar() {
        List<Arbitro> lista = arbitroRepository.findAll();
        var dtos = lista.stream()
                .map(a -> new ArbitroListDTO(
                        a.getId(),
                        a.getPessoa() != null ? a.getPessoa().getId() : null,
                        a.getPessoa() != null ? a.getPessoa().getNome() : null
                ))
                .toList();
        return ResponseEntity.ok(dtos);
    }
}
