package com.kickoff.api.controller;

import com.kickoff.api.dto.dashboard.DashboardGestorDTO;
import com.kickoff.api.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public ResponseEntity<DashboardGestorDTO> getDashboardGestor(
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(dashboardService.getGestorDashboard(email));
    }
}