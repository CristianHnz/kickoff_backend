package com.kickoff.api.dto.core;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquipeDTO(
        Long id,
        @NotBlank(message = "O nome da equipe é obrigatório")
        @Size(min = 3, message = "O nome deve ter pelo menos 3 caracteres")
        String nome,
        @NotBlank(message = "A cidade é obrigatória")
        String cidade,
        @NotBlank(message = "O estado é obrigatório")
        @Size(min = 2, max = 2, message = "Use a sigla do estado (ex: SP, RJ)")
        String estado
) {}