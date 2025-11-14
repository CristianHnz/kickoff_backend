package com.kickoff.api.dto.match;

import com.kickoff.api.model.match.CampeonatoStatus;
import java.time.LocalDate;

public record CampeonatoResponseDTO(
        Long id,
        String nome,
        Integer ano,
        LocalDate dataInicio,
        LocalDate dataFim,
        CampeonatoStatus status
) {}