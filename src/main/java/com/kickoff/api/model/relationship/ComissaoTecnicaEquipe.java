package com.kickoff.api.model.relationship;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.role.ComissaoTecnica;
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
@Table(name = "comissao_tecnica_x_equipe")
public class ComissaoTecnicaEquipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comissao_tecnica", nullable = false)
    private ComissaoTecnica comissaoTecnica;
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