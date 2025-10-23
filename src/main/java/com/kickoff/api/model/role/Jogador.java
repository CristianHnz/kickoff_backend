package com.kickoff.api.model.role;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.lookup.Posicao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jogador") // ATUALIZADO (era 'jogadores')
public class Jogador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false)
    @JoinColumn(name = "pessoa_id", unique = true, nullable = false)
    private Pessoa pessoa; // Coluna snake_case
    @ManyToOne(optional = false)
    @JoinColumn(name = "equipe_id", nullable = false)
    private Equipe equipe; // Coluna snake_case
    @Column(name = "numero_camisa")
    private Integer numeroCamisa; // Coluna snake_case
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "jogador_x_posicao", // ATUALIZADO para snake_case
            joinColumns = @JoinColumn(name = "id_jogador"), // Coluna snake_case
            inverseJoinColumns = @JoinColumn(name = "id_posicao") // Coluna snake_case
    )
    private Set<Posicao> posicoes = new HashSet<>();
}