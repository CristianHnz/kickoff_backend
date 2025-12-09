package com.kickoff.api.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record JogadorStatsDTO(
        Long totalGols,
        BigDecimal mediaGeral,
        BigDecimal mediaTecnica,
        BigDecimal mediaTatica,
        BigDecimal mediaFisica,
        List<BigDecimal> ultimasNotas
) {}