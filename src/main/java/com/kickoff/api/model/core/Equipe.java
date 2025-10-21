// src/main/java/com/kickoff/api/model/core/Equipe.java
package com.kickoff.api.model.core;

import com.kickoff.api.model.auth.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "equipe")
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    private String cidade;

    private String estado;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCriacao; // Seu script tinha 'DataCriacao'

    // Adição sugerida (baseada no seu script anterior e na lógica de 'GESTOR_EQUIPE')
    @ManyToOne
    @JoinColumn(name = "id_administrador")
    private Usuario administrador;
}