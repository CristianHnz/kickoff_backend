// src/main/java/com/kickoff/api/model/match/PartidaEvento.java
package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.role.Jogador;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "partida_evento")
public class PartidaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;

    @ManyToOne // Evento pode não ter jogador (ex: gol contra sem autoria)
    @JoinColumn(name = "jogador_id")
    private Jogador jogador;

    @ManyToOne // Para saber de qual equipe foi o evento (útil para gols contra)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento; // 'GOL', 'CARTAO_AMARELO', etc.

    private Integer minuto;

    private String descricao;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataHoraRegistro;
}