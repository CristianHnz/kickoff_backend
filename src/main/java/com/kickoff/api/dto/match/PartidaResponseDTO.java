package com.kickoff.api.dto.match;

import java.time.LocalDateTime;

public record PartidaResponseDTO(
        Long id,
        LocalDateTime dataHora,
        String local,
        String status,
        Integer placarCasa,
        Integer placarVisitante,
        PartidaCampeonatoDTO campeonato,
        PartidaEquipeDTO equipeCasa,
        PartidaEquipeDTO equipeVisitante,
        PartidaArbitroDTO arbitro
) {
}