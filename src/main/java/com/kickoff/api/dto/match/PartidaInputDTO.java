package com.kickoff.api.dto.match;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PartidaInputDTO(
        @NotNull(message = "Data e hora são obrigatórias")
        @Future(message = "A partida deve ser agendada para o futuro")
        LocalDateTime dataHora,
        @NotBlank(message = "O local é obrigatório")
        String local,
        @NotNull(message = "Time mandante obrigatório")
        Long equipeCasaId,
        @NotNull(message = "Time visitante obrigatório")
        Long equipeVisitanteId,
        Long arbitroId,
        Long campeonatoId
) {
}