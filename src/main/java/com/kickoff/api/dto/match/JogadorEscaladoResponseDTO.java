package com.kickoff.api.dto.match;
public record JogadorEscaladoResponseDTO(
        Long jogadorId,
        String nomeJogador,
        int numeroCamisa,
        String status,
        Long equipeId
) {}