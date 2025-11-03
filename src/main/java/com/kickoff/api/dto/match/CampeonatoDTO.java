package com.kickoff.api.dto.match;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CampeonatoDTO(
        @NotBlank(message = "O nome do campeonato é obrigatório")
        String nome,

        @NotNull(message = "O ano do campeonato é obrigatório")
        @Min(value = 2020, message = "O ano deve ser válido")
        Integer ano,

        @NotNull(message = "A data de início é obrigatória")
        @FutureOrPresent(message = "A data de início não pode ser no passado")
        LocalDate dataInicio,

        @NotNull(message = "A data de fim é obrigatória")
        @FutureOrPresent(message = "A data de fim não pode ser no passado")
        LocalDate dataFim
) {}