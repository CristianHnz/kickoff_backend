package com.kickoff.api.model.auth;

import com.kickoff.api.model.core.Pessoa;
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
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false) // Uma relação 1-para-1, e não pode ser nula
    @JoinColumn(name = "pessoa_id", unique = true, nullable = false)
    private Pessoa pessoa;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // Ex: "ROLE_ADMIN", "ROLE_JOGADOR"

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCadastro;
}