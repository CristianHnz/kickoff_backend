package com.kickoff.api.dto.match;

import com.kickoff.api.model.match.PartidaStatus;

import java.time.LocalDateTime;

public record PartidaResponseDTO(
        Long id,
        LocalDateTime dataHora,
        String local,
        String timeCasa,
        String timeVisitante,
        Integer placarCasa,
        Integer placarVisitante,
        PartidaStatus status,
        Long equipeCasaId,
        Long equipeVisitanteId,
        Long campeonatoId,
        String nomeCampeonato,
        int minJogadores,
        Integer duracaoTotalMinutos,
        LocalDateTime dataHoraInicioReal,
        String periodo,
        Long tempoJogadoSegundos,
        Long tipoPartidaId,
        String fase
) {
}