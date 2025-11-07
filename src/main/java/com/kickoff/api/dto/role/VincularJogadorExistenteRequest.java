package com.kickoff.api.dto.role;

import jakarta.validation.constraints.NotNull;

public record VincularJogadorExistenteRequest(
        @NotNull(message = "O ID do jogador é obrigatório")
        Long jogadorId
) {}
