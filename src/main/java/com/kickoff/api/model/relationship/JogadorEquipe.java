package com.kickoff.api.model.relationship;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.role.Jogador;
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
@Table(name = "jogador_x_equipe")
public class JogadorEquipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jogador", nullable = false)
    private Jogador jogador;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipe", nullable = false)
    private Equipe equipe;
    @Column(name = "data_entrada", nullable = false, updatable = false)
    private LocalDate dataEntrada;
    @Column(name = "data_saida")
    private LocalDate dataSaida;

    @PrePersist
    public void onPrePersist() {
        if (dataEntrada == null) {
            dataEntrada = LocalDate.now();
        }
    }
}