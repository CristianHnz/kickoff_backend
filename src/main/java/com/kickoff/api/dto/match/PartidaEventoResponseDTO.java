package com.kickoff.api.dto.match;

import java.time.LocalDateTime;

public record PartidaEventoResponseDTO(
        Long id,
        Long partidaId,
        String nomeJogador, // Nome da pessoa
        String nomeEquipe,
        String tipoEvento,
        Integer minuto,
        String descricao,
        LocalDateTime dataHoraRegistro
) {
}