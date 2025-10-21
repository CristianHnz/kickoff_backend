// src/main/java/com/kickoff/api/model/match/AvaliacaoPosJogo.java
package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.role.Jogador;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;

    @ManyToOne(optional = false)
    @JoinColumn(name = "jogador_id", nullable = false)
    private Jogador jogador;

    @ManyToOne // O avaliador é uma Pessoa (provavelmente da comissão técnica)
    @JoinColumn(name = "avaliador_id")
    private Pessoa avaliador;

    @Column(nullable = false, precision = 3, scale = 1)
    private BigDecimal nota; // Ex: 8.5

    private String comentarios;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataAvaliacao;
}