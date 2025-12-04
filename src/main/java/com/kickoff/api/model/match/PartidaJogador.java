package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.role.Jogador;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partida_jogador", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"partida_id", "jogador_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class PartidaJogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partida_id")
    private Partida partida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jogador_id")
    private Jogador jogador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @Column(name = "numero_camisa", nullable = false)
    private Integer numeroCamisa;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_jogador", nullable = false)
    private StatusJogadorPartida statusJogador;
}