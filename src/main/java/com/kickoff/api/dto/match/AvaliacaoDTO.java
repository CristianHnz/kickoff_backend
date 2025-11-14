package com.kickoff.api.dto.match;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AvaliacaoDTO(
        Long id,
        @NotNull Long partidaId,
        @NotNull Long jogadorId,
        String nomeJogador,
        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("10.0")
        BigDecimal nota,
        String comentarios
) {}