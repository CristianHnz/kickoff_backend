package com.kickoff.api.model.lookup;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_partida")
@Getter @Setter @NoArgsConstructor
public class TipoPartida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String descricao;

    @Column(name = "min_jogadores", nullable = false)
    private int minJogadores;

    @Column(name = "duracao_minutos", nullable = false)
    private int duracaoMinutos;
}