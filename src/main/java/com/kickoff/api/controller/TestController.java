package com.kickoff.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public String allAccess() {
        return "Olá! Esta é uma rota pública (mas autenticada).";
    }

    @GetMapping("/jogador")
    @PreAuthorize("hasRole('JOGADOR')")
    public String jogadorAccess() {
        return "Olá Jogador!";
    }

    @GetMapping("/gestor")
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public String gestorAccess() {
        return "Olá Gestor!";
    }
}