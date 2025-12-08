package com.kickoff.api.dto.match;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CampeonatoInputDTO(
        @NotBlank(message = "O nome do campeonato é obrigatório")
        String nome,
        @NotNull(message = "O ano é obrigatório")
        @Min(value = 2024, message = "O ano deve ser atual ou futuro")
        Integer ano,
        @NotNull(message = "Data de início é obrigatória")
        LocalDate dataInicio,
        @NotNull(message = "Data de término é obrigatória")
        LocalDate dataFim,
        String tipo,
        @NotNull Integer minEquipes,
        @NotNull Boolean idaEVolta,
        Long tipoPartidaId
) {}