package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Equipe;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "campeonato_x_equipe",
        uniqueConstraints = @UniqueConstraint(name = "uk_campeonato_equipe", columnNames = {"campeonato_id", "equipe_id"})
)
public class CampeonatoEquipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "campeonato_id", nullable = false)
    private Campeonato campeonato;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipe_id", nullable = false)
    private Equipe equipe;
}
