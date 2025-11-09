package com.kickoff.api.dto.match;

import com.kickoff.api.model.match.PartidaStatus;

import java.time.LocalDateTime;

public record PartidaResponseDTO(
        Long id,
        LocalDateTime dataHora,
        String local,
        PartidaStatus status,
        Integer placarCasa,
        Integer placarVisitante,
        PartidaCampeonatoDTO campeonato,
        PartidaEquipeDTO equipeCasa,
        PartidaEquipeDTO equipeVisitante,
        PartidaArbitroDTO arbitro
) {
}