package com.kickoff.api.dto.match;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AvaliacaoResponseDTO(
        Long id,
        Long partidaId,
        String nomeJogador,
        String nomeAvaliador,
        BigDecimal nota,
        String comentarios,
        LocalDateTime dataAvaliacao
) {
}