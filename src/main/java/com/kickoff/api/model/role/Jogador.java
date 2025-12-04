package com.kickoff.api.model.role;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.relationship.JogadorEquipe;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.lookup.Posicao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jogador")
public class Jogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", unique = true, nullable = false)
    private Pessoa pessoa;
    @Column(name = "numero_camisa")
    private Integer numeroCamisa;
    private Double altura;
    private Double peso;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "jogador_x_posicao",
            joinColumns = @JoinColumn(name = "id_jogador"),
            inverseJoinColumns = @JoinColumn(name = "id_posicao")
    )
    private Set<Posicao> posicoes = new HashSet<>();
    @OneToMany(
            mappedBy = "jogador",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<JogadorEquipe> historicoEquipes = new HashSet<>();
    public Equipe getEquipeAtual() {
        if (historicoEquipes == null) {
            return null;
        }
        return historicoEquipes.stream()
                .filter(he -> he.getDataSaida() == null)
                .map(JogadorEquipe::getEquipe)
                .findFirst()
                .orElse(null);
    }
}