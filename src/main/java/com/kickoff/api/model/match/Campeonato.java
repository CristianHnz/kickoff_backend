package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.lookup.TipoPartida;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "campeonato")
public class Campeonato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nome;
    @Column(nullable = false)
    private Integer ano;
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CampeonatoStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoCampeonato tipo = TipoCampeonato.PONTOS_CORRIDOS;
    @Column(name = "min_equipes")
    private Integer minEquipes;
    @Column(name = "ida_e_volta")
    private Boolean idaEVolta;
    @OneToOne
    @JoinColumn(name = "equipe_campeada_id")
    private Equipe equipeCampeada;
    @ManyToOne
    @JoinColumn(name = "tipo_partida_id")
    private TipoPartida tipoPartida;
    @PrePersist
    protected void onPersist() {
        if (this.status == null) {
            this.status = CampeonatoStatus.AGENDADO;
        }
    }
}