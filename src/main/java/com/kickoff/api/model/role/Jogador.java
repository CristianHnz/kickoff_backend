// src/main/java/com/kickoff/api/model/role/Jogador.java
package com.kickoff.api.model.role;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.lookup.Posicao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jogador")
public class Jogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "pessoaid", unique = true, nullable = false) // Coluna 'PessoaId' do seu script
    private Pessoa pessoa;

    @ManyToOne(optional = false) // Muitos jogadores para UMA equipe
    @JoinColumn(name = "equipeid", nullable = false) // Coluna 'EquipeId' do seu script
    private Equipe equipe;

    @Column(name = "numerocamisa")
    private Integer numeroCamisa;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER para carregar as posições sempre junto com o jogador
    @JoinTable(
            name = "jogadorxposicao", // Nome da tabela de junção do seu script
            joinColumns = @JoinColumn(name = "idjogador"), // Chave estrangeira para Jogador
            inverseJoinColumns = @JoinColumn(name = "idposicao") // Chave estrangeira para Posicao
    )
    private Set<Posicao> posicoes = new HashSet<>();
}