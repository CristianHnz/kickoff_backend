package com.kickoff.api.dto.match;
import com.kickoff.api.model.match.StatusJogadorPartida;
import jakarta.validation.constraints.NotNull;
public record JogadorEscaladoInputDTO(
        @NotNull Long jogadorId,
        @NotNull Integer numeroCamisa,
        @NotNull StatusJogadorPartida status
) {}