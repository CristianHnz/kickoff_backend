package com.kickoff.api.dto.dashboard;

import java.math.BigDecimal;

public record JogadorStatsDTO(
        Long totalGols,
        BigDecimal mediaAvaliacoes
) {}