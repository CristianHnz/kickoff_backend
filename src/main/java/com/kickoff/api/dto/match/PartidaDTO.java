package com.kickoff.api.dto.match;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record PartidaDTO(
        Long campeonatoId,

        @NotNull(message = "O ID da equipe da casa é obrigatório")
        Long equipeCasaId,

        @NotNull(message = "O ID da equipe visitante é obrigatória")
        Long equipeVisitanteId,

        Long arbitroId,

        @NotNull(message = "A data e hora são obrigatórias")
        @FutureOrPresent(message = "A partida não pode ser agendada no passado")
        LocalDateTime dataHora,

        @NotNull(message = "O local é obrigatório")
        String local
) {}
