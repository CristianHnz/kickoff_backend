// src/main/java/com/kickoff/api/dto/match/PartidaResultadoDTO.java
package com.kickoff.api.dto.match;

import com.kickoff.api.model.match.PartidaStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PartidaResultadoDTO(
        @NotNull @Min(0) Integer placarCasa,
        @NotNull @Min(0) Integer placarVisitante,
        @NotNull PartidaStatus status
) {}
