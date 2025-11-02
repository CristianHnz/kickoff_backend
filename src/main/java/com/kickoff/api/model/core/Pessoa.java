package com.kickoff.api.model.core;

import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.lookup.TipoPessoa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
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
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    @Column(unique = true)
    private String cpf;
    @Column(unique = true)
    private String email;
    private String telefone;
    @ManyToOne
    @JoinColumn(name = "tipo_pessoa")
    private TipoPessoa tipoPessoa;
    @CreationTimestamp
    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;
    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Usuario usuario;
}