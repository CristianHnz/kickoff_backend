package com.kickoff.api.dto.role;
import com.kickoff.api.model.lookup.Posicao;
import java.util.Set;

public record JogadorResponseDTO(
        Long id,
        Long pessoaId,
        String nomePessoa,
        String emailPessoa,
        Integer numeroCamisa,
        String nomeEquipe,
        Set<Posicao> posicoes
) {
}
