package com.kickoff.api.security;

import com.kickoff.api.service.TokenService;
import io.jsonwebtoken.Claims; // Importe o Claims
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Importe
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = this.recoverToken(request);

        if (token != null) {
            Claims claims = tokenService.validateToken(token);
            String email = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);

            System.out.println("--- SECURITY FILTER DEBUG ---");
            System.out.println("Usuário (email): " + email);
            System.out.println("Roles do Token: " + roles);

            if (email != null && roles != null && !roles.isEmpty()) {
                var authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                System.out.println("Autoridades Criadas: " + authorities);

                var authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("Usuário ou Roles nulos. Autenticação falhou.");
            }
            System.out.println("-----------------------------");
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}