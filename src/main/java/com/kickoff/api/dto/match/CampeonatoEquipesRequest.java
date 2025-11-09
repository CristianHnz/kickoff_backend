package com.kickoff.api.dto.match;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CampeonatoEquipesRequest(
        @NotEmpty(message = "Informe ao menos uma equipe")
        List<Long> equipeIds
) {}