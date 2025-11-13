// src/main/java/com/kickoff/api/model/match/PartidaStatus.java
package com.kickoff.api.model.match;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PartidaStatus {
    AGENDADA,
    CONFIRMADA,
    EM_ANDAMENTO,
    FINALIZADA,
    CANCELADA,
    WO;

    @JsonCreator
    public static PartidaStatus from(String value) {
        if (value == null) return null;
        String norm = value
                .trim()
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase();
        return PartidaStatus.valueOf(norm);
    }
}
