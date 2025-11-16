package com.kickoff.api.controller;

import com.kickoff.api.dto.role.ContratacaoStaffDTO;
import com.kickoff.api.dto.role.StaffResumoDTO;
import com.kickoff.api.service.ComissaoTecnicaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class ComissaoTecnicaController {

    @Autowired
    private ComissaoTecnicaService staffService;

    @GetMapping("/disponiveis")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<List<StaffResumoDTO>> listarDisponiveis() {
        return ResponseEntity.ok(staffService.listarDisponiveis());
    }

    @GetMapping("/equipe/{equipeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StaffResumoDTO>> listarDaEquipe(@PathVariable Long equipeId) {
        return ResponseEntity.ok(staffService.listarDaEquipe(equipeId));
    }

    @PostMapping("/equipe/{equipeId}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> contratar(
            @PathVariable Long equipeId,
            @Valid @RequestBody ContratacaoStaffDTO dto
    ) {
        staffService.contratarStaff(equipeId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/equipe/{equipeId}/{comissaoId}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> dispensar(
            @PathVariable Long equipeId,
            @PathVariable Long comissaoId
    ) {
        staffService.dispensarStaff(equipeId, comissaoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StaffResumoDTO>> listarTodos() {
        return ResponseEntity.ok(staffService.listarTodos());
    }

    @PutMapping("/{id}/funcao")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> atualizarFuncao(@PathVariable Long id, @RequestBody String funcao) {
        String funcaoLimpa = funcao.replace("\"", "");
        staffService.atualizarFuncao(id, funcaoLimpa);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<Void> deletarMembro(@PathVariable Long id) {
        staffService.deletarMembro(id);
        return ResponseEntity.noContent().build();
    }
}