package com.kickoff.api.dto.dashboard;

import com.kickoff.api.dto.match.PartidaResponseDTO;
import com.kickoff.api.dto.role.EquipeAtualDTO;

public record DashboardJogadorDTO(
        EquipeAtualDTO equipeAtual,
        PartidaResponseDTO proximoJogo,
        PartidaResponseDTO ultimoResultado,
        JogadorStatsDTO estatisticas
) {
}