package com.kickoff.api.dto.team;

public record EquipeStatsDTO(
        long totalJogos,
        long vitorias,
        long empates,
        long derrotas,
        long golsPro,
        long golsContra,
        long saldoGols,
        double aproveitamento
) {}