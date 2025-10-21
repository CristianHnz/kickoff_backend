// src/main/java/com/kickoff/api/model/match/Partida.java
package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.role.Arbitro;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "partida")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Várias partidas em UM campeonato
    @JoinColumn(name = "campeonato_id")
    private Campeonato campeonato;

    @ManyToOne(optional = false) // Várias partidas para UMA equipe (como casa)
    @JoinColumn(name = "equipe_casa_id", nullable = false)
    private Equipe equipeCasa;

    @ManyToOne(optional = false) // Várias partidas para UMA equipe (como visitante)
    @JoinColumn(name = "equipe_visitante_id", nullable = false)
    private Equipe equipeVisitante;

    @ManyToOne // Várias partidas para UM árbitro
    @JoinColumn(name = "arbitro_id")
    private Arbitro arbitro;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    private String local;

    private Integer placarCasa;

    private Integer placarVisitante;

    @Column(length = 50)
    private String status; // 'AGENDADA', 'EM_ANDAMENTO', 'FINALIZADA'

    @PrePersist
    protected void onPersist() {
        if (this.placarCasa == null) {
            this.placarCasa = 0;
        }
        if (this.placarVisitante == null) {
            this.placarVisitante = 0;
        }
        if (this.status == null) {
            this.status = "AGENDADA";
        }
    }
}