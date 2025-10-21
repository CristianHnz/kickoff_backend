// src/main/java/com/kickoff/api/model/match/Campeonato.java
package com.kickoff.api.model.match;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "campeonato")
public class Campeonato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFim;

    @Column(length = 50)
    private String status; // 'AGENDADO', 'EM_ANDAMENTO', 'FINALIZADO'

    @PrePersist
    protected void onPersist() {
        if (this.status == null) {
            this.status = "AGENDADO";
        }
    }
}