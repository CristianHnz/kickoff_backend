package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.role.Jogador;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "partida_evento")
public class PartidaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogador_id")
    private Jogador jogador;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 50)
    private PartidaEventoTipo tipoEvento;
    private Integer minuto;
    private String descricao;
    @CreationTimestamp
    @Column(name = "data_hora_registro", updatable = false)
    private LocalDateTime dataHoraRegistro;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogador_assistencia_id")
    private Jogador jogadorAssistencia;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogador_substituido_id")
    private Jogador jogadorSubstituido;
}