package com.kickoff.api.controller;

import com.kickoff.api.model.auth.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public ResponseEntity<String> getHelloMessage() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        String nomeDoUsuario = usuarioLogado.getPessoa().getNome();
        String roleDoUsuario = usuarioLogado.getRole();

        String mensagem = String.format(
                "Olá, %s! Você está autenticado com sucesso. O seu email é %s e a sua permissão é %s.",
                nomeDoUsuario,
                usuarioLogado.getUsername(),
                roleDoUsuario
        );

        return ResponseEntity.ok(mensagem);
    }
}