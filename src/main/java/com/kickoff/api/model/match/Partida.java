package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.role.Arbitro;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "partida")
public class Partida {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "campeonato_id")
    private Campeonato campeonato; // pode ser null (amistoso)

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipe_casa_id", nullable = false)
    private Equipe equipeCasa;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipe_visitante_id", nullable = false)
    private Equipe equipeVisitante;

    @ManyToOne @JoinColumn(name = "arbitro_id")
    private Arbitro arbitro; // pode ser null

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

    @PrePersist
    protected void onPersist() {
        if (this.placarCasa == null) this.placarCasa = 0;
        if (this.placarVisitante == null) this.placarVisitante = 0;
        if (this.status == null) this.status = PartidaStatus.AGENDADA;
    }
}
