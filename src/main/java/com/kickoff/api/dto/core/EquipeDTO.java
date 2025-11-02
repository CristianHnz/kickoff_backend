package com.kickoff.api.dto.core;

import jakarta.validation.constraints.NotBlank;

public record EquipeDTO(
        @NotBlank(message = "O nome da equipe é obrigatório")
        String nome,

        @NotBlank(message = "A cidade é obrigatória")
        String cidade,

        @NotBlank(message = "O estado é obrigatório")
        String estado
) {
}