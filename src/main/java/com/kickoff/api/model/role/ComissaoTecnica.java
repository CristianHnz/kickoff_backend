// src/main/java/com/kickoff/api/model/role/ComissaoTecnica.java
package com.kickoff.api.model.role;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.core.Pessoa;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comissaotecnica") // Seu script usou "ComissaoTecnica"
public class ComissaoTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "idpes", unique = true, nullable = false) // Coluna 'IdPes' do seu script
    private Pessoa pessoa;

    @ManyToOne(optional = false) // Muitos membros da comissão para UMA equipe
    @JoinColumn(name = "equipe_id", nullable = false)
    private Equipe equipe;

    @Column(nullable = false)
    private String funcao; // 'TECNICO', 'AUXILIAR', etc.

    @Column(name = "data_entrada_equipe")
    private LocalDate dataEntradaEquipe;

    @PrePersist
    protected void onEnterTeam() {
        if (this.dataEntradaEquipe == null) {
            this.dataEntradaEquipe = LocalDate.now();
        }
    }
}