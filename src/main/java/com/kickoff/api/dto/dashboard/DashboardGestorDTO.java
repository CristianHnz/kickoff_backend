package com.kickoff.api.dto.dashboard;

import com.kickoff.api.dto.core.EquipeDTO;
import com.kickoff.api.dto.match.PartidaResponseDTO;
import com.kickoff.api.dto.match.TabelaCampeonatoDTO;

import java.util.List;

public record DashboardGestorDTO(
        EquipeDTO minhaEquipe,
        PartidaResponseDTO proximoJogo,
        PartidaResponseDTO ultimoResultado,
        List<TabelaCampeonatoDTO> classificacoes
) {
}