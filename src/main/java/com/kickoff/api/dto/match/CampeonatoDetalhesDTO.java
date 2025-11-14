package com.kickoff.api.dto.match;

import java.util.List;

public record CampeonatoDetalhesDTO(
        CampeonatoResponseDTO campeonatoInfo,
        List<TabelaCampeonatoDTO> tabela
) {
}