package com.kickoff.api.dto.role;

import java.util.List;

public record JogadorResumoDTO(
        Long id,
        Long pessoaId,
        String nome,
        Integer numeroCamisa,
        List<String> posicoes,
        String status,
        Long equipeId,
        String nomeEquipe
) {
}