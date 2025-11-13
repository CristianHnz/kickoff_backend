package com.kickoff.api.dto.match;

import jakarta.validation.constraints.NotNull;

public record PartidaEventoDTO(
        Long id,
        @NotNull Long partidaId,
        Long jogadorId,
        Long equipeId,
        @NotNull String tipoEvento,
        Integer minuto,
        String descricao,
        Long jogadorAssistenciaId,
        Long jogadorSubstituidoId
) {
}