package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.lookup.TipoPartida;
import com.kickoff.api.model.role.Arbitro;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "partida")
public class Partida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campeonato_id")
    private Campeonato campeonato; // pode ser null (amistoso)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipe_casa_id", nullable = false)
    private Equipe equipeCasa;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipe_visitante_id", nullable = false)
    private Equipe equipeVisitante;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arbitro_id")
    private Arbitro arbitro;
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
    private String local;
    @Column(name = "placar_casa")
    private Integer placarCasa;
    @Column(name = "placar_visitante")
    private Integer placarVisitante;
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PartidaStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_partida_id")
    private TipoPartida tipoPartida;

    @PrePersist
    protected void onPersist() {
        if (this.placarCasa == null) this.placarCasa = 0;
        if (this.placarVisitante == null) this.placarVisitante = 0;
        if (this.status == null) this.status = PartidaStatus.AGENDADA;
    }
}