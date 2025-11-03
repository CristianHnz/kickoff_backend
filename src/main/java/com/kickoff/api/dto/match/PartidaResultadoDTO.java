package com.kickoff.api.dto.match;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PartidaResultadoDTO(
        @NotNull(message = "O placar da casa é obrigatório")
        @Min(value = 0, message = "O placar não pode ser negativo")
        Integer placarCasa,

        @NotNull(message = "O placar visitante é obrigatório")
        @Min(value = 0, message = "O placar não pode ser negativo")
        Integer placarVisitante,

        @NotBlank(message = "O status da partida é obrigatório")
        String status
) {
}