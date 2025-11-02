package com.kickoff.api.dto.role;
import com.kickoff.api.model.lookup.Posicao;
import java.util.Set;

public record JogadorResponseDTO(
        Long idJogador,
        Long idPessoa,
        String nome,
        String email,
        Integer numeroCamisa,
        String nomeEquipe,
        Set<Posicao> posicoes
) {
}