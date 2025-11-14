package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Equipe;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "campeonato_x_equipe", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"campeonato_id", "equipe_id"})
})
@Getter @Setter @NoArgsConstructor
public class CampeonatoEquipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campeonato_id")
    private Campeonato campeonato;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @Column(nullable = false)
    private int pontos = 0;
    @Column(nullable = false)
    private int vitorias = 0;
    @Column(nullable = false)
    private int empates = 0;
    @Column(nullable = false)
    private int derrotas = 0;
    @Column(name = "gols_pro", nullable = false)
    private int golsPro = 0;
    @Column(name = "gols_contra", nullable = false)
    private int golsContra = 0;
}