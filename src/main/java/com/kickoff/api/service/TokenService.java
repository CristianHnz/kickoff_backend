// src/main/java/com/kickoff/api/service/TokenService.java
package com.kickoff.api.service;

import com.kickoff.api.model.auth.Usuario;
import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm; // Não é mais necessário
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date; // Importar java.util.Date

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Usuario usuario) {
        Instant expirationTime = LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));

        return Jwts.builder()
                .issuer("Kickoff API")
                .subject(usuario.getUsername()) // O email do usuário
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expirationTime))
                .signWith(getSigningKey()) // A chave já contém o algoritmo
                .compact();
    }

    /**
     * Valida o token e retorna o "subject" (email) se for válido.
     */
    public String validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Token JWT inválido ou expirado!", e);
        }
    }
}