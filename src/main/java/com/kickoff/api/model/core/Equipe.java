package com.kickoff.api.model.core;

import com.kickoff.api.model.auth.Usuario;
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
@Table(name = "equipe") // ATUALIZADO (era 'equipes')
public class Equipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nome;
    private String cidade;
    private String estado;
    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao; // Coluna snake_case
    @ManyToOne
    @JoinColumn(name = "id_administrador")
    private Usuario administrador;
}