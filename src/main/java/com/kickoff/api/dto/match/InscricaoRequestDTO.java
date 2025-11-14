package com.kickoff.api.dto.match;

import jakarta.validation.constraints.NotNull;

public record InscricaoRequestDTO(@NotNull Long equipeId) {
}