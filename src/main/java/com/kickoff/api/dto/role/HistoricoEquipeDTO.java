package com.kickoff.api.dto.role;

import java.time.LocalDate;

public record HistoricoEquipeDTO(
        String nomeEquipe,
        LocalDate dataEntrada,
        LocalDate dataSaida
) {}