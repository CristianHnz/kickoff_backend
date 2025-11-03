package com.kickoff.api.dto.match;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AvaliacaoDTO(
        @NotNull(message = "O ID da partida é obrigatório")
        Long partidaId,

        @NotNull(message = "O ID do jogador (vínculo) é obrigatório")
        Long jogadorId,

        @NotNull(message = "A nota é obrigatória")
        @DecimalMin(value = "0.0", message = "A nota deve ser no mínimo 0.0")
        @DecimalMax(value = "10.0", message = "A nota deve ser no máximo 10.0")
        BigDecimal nota,

        String comentarios
) {
}