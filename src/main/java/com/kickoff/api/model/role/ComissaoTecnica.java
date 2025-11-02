package com.kickoff.api.model.role;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.core.Pessoa;
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
public class ComissaoTecnica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false)
    @JoinColumn(name = "id_pes", unique = true, nullable = false)
    private Pessoa pessoa;
    @ManyToOne(optional = false)
    @JoinColumn(name = "equipe_id", nullable = false)
    private Equipe equipe;
    @Column(nullable = false)
    private String funcao;
    @Column(name = "data_entrada_equipe")
    private LocalDate dataEntradaEquipe;

    @PrePersist
    protected void onEnterTeam() {
        if (this.dataEntradaEquipe == null) this.dataEntradaEquipe = LocalDate.now();
    }
}