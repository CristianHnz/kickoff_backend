package com.kickoff.api.dto.role;

import java.time.LocalDate;
import java.util.List;

public record JogadorDetalhesDTO(
        Long jogadorId,
        String nome,
        String email,
        String cpf,
        String telefone,
        LocalDate dataNascimento,
        List<String> posicoes,
        EquipeAtualDTO equipeAtual,
        List<HistoricoEquipeDTO> historico
) {
}