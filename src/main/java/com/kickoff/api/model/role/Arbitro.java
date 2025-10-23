package com.kickoff.api.model.role;

import com.kickoff.api.model.core.Pessoa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "arbitro") // ATUALIZADO (era 'arbitros')
public class Arbitro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false)
    @JoinColumn(name = "pessoa_id", unique = true, nullable = false)
    private Pessoa pessoa;
    @Column(name = "licenca_cbf", unique = true)
    private String licencaCbf;
    @CreationTimestamp
    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;
}