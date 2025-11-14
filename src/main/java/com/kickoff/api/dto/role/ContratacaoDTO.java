package com.kickoff.api.dto.role;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record ContratacaoDTO(
        @NotNull Long jogadorId,
        @NotNull Integer numeroCamisa,
        @NotBlank String posicao
) {}