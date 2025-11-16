package com.kickoff.api.dto.match;

public record ArtilhariaDTO(
        Long jogadorId,
        String nomeJogador,
        String nomeEquipe,
        Long gols
) {}