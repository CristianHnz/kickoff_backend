package com.kickoff.api.model.role;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.core.Pessoa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
// @Table(name = "comissaoTecnica") REMOVIDO! Spring agora acerta sozinho.
public class ComissaoTecnica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false)
    @JoinColumn(name = "id_pes", unique = true, nullable = false)
    private Pessoa pessoa; // Coluna snake_case
    @ManyToOne(optional = false)
    @JoinColumn(name = "equipe_id", nullable = false)
    private Equipe equipe;
    @Column(nullable = false)
    private String funcao;
    @Column(name = "data_entrada_equipe")
    private LocalDate dataEntradaEquipe; // Coluna snake_case

    @PrePersist
    protected void onEnterTeam() {
        if (this.dataEntradaEquipe == null) this.dataEntradaEquipe = LocalDate.now();
    }
}