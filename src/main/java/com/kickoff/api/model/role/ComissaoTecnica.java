package com.kickoff.api.model.role;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.relationship.ComissaoTecnicaEquipe;
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
@Table(name = "comissao_tecnica")
public class ComissaoTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pes", unique = true, nullable = false)
    private Pessoa pessoa;
    @Column(nullable = false)
    private String funcao;
    @OneToMany(
            mappedBy = "comissaoTecnica",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<ComissaoTecnicaEquipe> historicoEquipes = new HashSet<>();
    public Equipe getEquipeAtual() {
        if (historicoEquipes == null) {
            return null;
        }
        return historicoEquipes.stream()
                .filter(he -> he.getDataSaida() == null)
                .map(ComissaoTecnicaEquipe::getEquipe)
                .findFirst()
                .orElse(null);
    }
}