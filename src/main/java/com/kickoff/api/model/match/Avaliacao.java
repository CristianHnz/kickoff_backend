package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.role.Jogador;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
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
    @ManyToOne
    @JoinColumn(name = "avaliador_id")
    private Pessoa avaliador;
    @Column(nullable = false, precision = 3, scale = 1)
    private BigDecimal nota;
    private String comentarios;
    @CreationTimestamp
    @Column(name = "data_avaliacao", updatable = false)
    private LocalDateTime dataAvaliacao;
}