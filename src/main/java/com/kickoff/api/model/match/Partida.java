package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.role.Arbitro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data; /* ... (imports) ... */
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "partida") // ATUALIZADO (era 'partidas')
public class Partida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "campeonato_id")
    private Campeonato campeonato;
    @ManyToOne(optional = false)
    @JoinColumn(name = "equipe_casa_id", nullable = false)
    private Equipe equipeCasa;
    @ManyToOne(optional = false)
    @JoinColumn(name = "equipe_visitante_id", nullable = false)
    private Equipe equipeVisitante;
    @ManyToOne
    @JoinColumn(name = "arbitro_id")
    private Arbitro arbitro;
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora; // Coluna snake_case
    private String local;
    @Column(name = "placar_casa")
    private Integer placarCasa; // Coluna snake_case
    @Column(name = "placar_visitante")
    private Integer placarVisitante; // Coluna snake_case
    @Column(length = 50)
    private String status;

    @PrePersist
    protected void onPersist() {
        if (this.placarCasa == null) this.placarCasa = 0;
        if (this.placarVisitante == null) this.placarVisitante = 0;
        if (this.status == null) this.status = "AGENDADA";
    }
}