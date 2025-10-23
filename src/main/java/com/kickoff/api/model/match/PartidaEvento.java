package com.kickoff.api.model.match;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.role.Jogador;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data; /* ... (imports) ... */
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
// @Table(name = "partidaEvento") REMOVIDO! Spring agora acerta sozinho.
public class PartidaEvento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;
    @ManyToOne
    @JoinColumn(name = "jogador_id")
    private Jogador jogador;
    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;
    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento;
    private Integer minuto;
    private String descricao;
    @CreationTimestamp
    @Column(name = "data_hora_registro", updatable = false)
    private LocalDateTime dataHoraRegistro; // Coluna snake_case
}