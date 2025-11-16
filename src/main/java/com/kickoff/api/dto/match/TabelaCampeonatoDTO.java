package com.kickoff.api.dto.match;

public record TabelaCampeonatoDTO(
        Long campeonatoId,
        String campeonatoNome,
        Integer posicao,
        Long equipeId,
        String nomeEquipe,
        int pontos,
        int vitorias,
        int empates,
        int derrotas,
        int golsPro,
        int golsContra,
        int saldoGols,
        int jogos
) {
}