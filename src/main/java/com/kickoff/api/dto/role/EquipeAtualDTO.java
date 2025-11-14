package com.kickoff.api.dto.role;

import java.time.LocalDate;

public record EquipeAtualDTO(
        Long equipeId,
        String nomeEquipe,
        Integer numeroCamisa,
        LocalDate dataEntrada
) {}