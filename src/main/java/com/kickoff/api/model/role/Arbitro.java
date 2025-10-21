// src/main/java/com/kickoff/api/model/role/Arbitro.java
package com.kickoff.api.model.role;

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
@Table(name = "arbitro")
public class Arbitro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false) // Um árbitro É uma pessoa
    @JoinColumn(name = "pessoa_id", unique = true, nullable = false)
    private Pessoa pessoa;

    @Column(name = "licenca_cbf", unique = true)
    private String licencaCbf;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCadastro;
}