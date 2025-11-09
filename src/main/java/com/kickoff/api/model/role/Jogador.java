package com.kickoff.api.model.role;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.lookup.Posicao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jogador")
public class Jogador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false)
    @JoinColumn(name = "pessoa_id", unique = true, nullable = false)
    private Pessoa pessoa;
    @ManyToOne(optional = true)
    @JoinColumn(name = "equipe_id", nullable = true)
    private Equipe equipe;
    @Column(name = "numero_camisa")
    private Integer numeroCamisa;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "jogador_x_posicao",
            joinColumns = @JoinColumn(name = "id_jogador"),
            inverseJoinColumns = @JoinColumn(name = "id_posicao")
    )
    private Set<Posicao> posicoes = new HashSet<>();
}