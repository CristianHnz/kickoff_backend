package com.kickoff.api.controller;

import com.kickoff.api.dto.match.CampeonatoDTO;
import com.kickoff.api.dto.match.CampeonatoEquipesRequest;
import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.service.CampeonatoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campeonatos")
public class CampeonatoController {

    @Autowired
    private CampeonatoService campeonatoService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> criarCampeonato(@Valid @RequestBody CampeonatoDTO dto) {
        try {
            Campeonato novoCampeonato = campeonatoService.criarCampeonato(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoCampeonato);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Campeonato>> listarCampeonatos() {
        List<Campeonato> campeonatos = campeonatoService.listarCampeonatos();
        return ResponseEntity.ok(campeonatos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarCampeonatoPorId(@PathVariable Long id) {
        try {
            Campeonato campeonato = campeonatoService.buscarCampeonatoPorId(id);
            return ResponseEntity.ok(campeonato);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Qualquer utilizador autenticado pode atualizar
    public ResponseEntity<?> atualizarCampeonato(@PathVariable Long id, @Valid @RequestBody CampeonatoDTO dto) {
        try {
            Campeonato campeonatoAtualizado = campeonatoService.atualizarCampeonato(id, dto);
            return ResponseEntity.ok(campeonatoAtualizado); // Retorna 200 OK com o objeto atualizado

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletarCampeonato(@PathVariable Long id) {
        try {
            campeonatoService.deletarCampeonato(id);
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não é possível excluir o campeonato. Ele já está associado a partidas.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir campeonato.");
        }
    }

    @PostMapping("/{id}/equipes")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<?> adicionarEquipesAoCampeonato(
            @PathVariable Long id,
            @Valid @RequestBody CampeonatoEquipesRequest body
    ) {
        try {
            campeonatoService.adicionarEquipes(id, body.equipeIds());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao adicionar equipes.");
        }
    }

//    @PostMapping("/{id}/gerar-partidas")
//    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
//    public ResponseEntity<?> gerarPartidasTurnoUnico(@PathVariable Long id) {
//        try {
//            int qtd = campeonatoService.gerarPartidasTurnoUnico(id);
//            return ResponseEntity.ok("Partidas geradas: " + qtd);
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar partidas.");
//        }
//    }
}