package com.kickoff.api.dto.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PartidaUpdateDTO {
    @Nullable public Long campeonatoId;    // null => remover campeonato
    @Nullable public Long equipeCasaId;    // não pode ser null quando presente
    @Nullable public Long equipeVisitanteId; // idem
    @Nullable public Long arbitroId;       // null => remover árbitro
    @Nullable public LocalDateTime dataHora;
    @Nullable public String local;
}
