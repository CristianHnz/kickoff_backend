package com.kickoff.api.model.core;

import com.kickoff.api.model.lookup.TipoPessoa;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pessoa")
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private LocalDate dataNascimento;

    @Column(unique = true)
    private String cpf;

    @Column(unique = true)
    private String email;

    private String telefone;

    @ManyToOne // Muitas pessoas podem ter um tipo
    @JoinColumn(name = "tipoPessoa") // Nome da coluna no seu script
    private TipoPessoa tipoPessoa;

    @CreationTimestamp // O Hibernate gerencia isso para nós
    @Column(updatable = false)
    private LocalDateTime dataCadastro;
}