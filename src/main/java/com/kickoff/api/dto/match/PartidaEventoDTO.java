package com.kickoff.api.dto.match;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PartidaEventoDTO(
        @NotNull(message = "O ID do jogador é obrigatório (pode ser nulo para eventos de equipe)")
        Long jogadorId,

        @NotNull(message = "O ID da equipe é obrigatório")
        Long equipeId,

        @NotBlank(message = "O tipo do evento é obrigatório (ex: GOL, CARTAO_AMARELO)")
        String tipoEvento,

        @NotNull(message = "O minuto do evento é obrigatório")
        Integer minuto,

        String descricao
) {
}