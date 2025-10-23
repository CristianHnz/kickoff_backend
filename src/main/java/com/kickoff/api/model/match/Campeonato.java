package com.kickoff.api.model.match;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data; /* ... (imports) ... */
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "campeonato") // ATUALIZADO (era 'campeonatos')
public class Campeonato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nome;
    @Column(nullable = false)
    private Integer ano;
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio; // Coluna snake_case
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim; // Coluna snake_case
    @Column(length = 50)
    private String status;

    @PrePersist
    protected void onPersist() {
        if (this.status == null) this.status = "AGENDADO";
    }
}