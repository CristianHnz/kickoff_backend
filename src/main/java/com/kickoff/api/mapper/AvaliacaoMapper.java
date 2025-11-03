package com.kickoff.api.mapper;

import com.kickoff.api.dto.match.AvaliacaoResponseDTO;
import com.kickoff.api.model.match.Avaliacao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvaliacaoMapper {

    public AvaliacaoResponseDTO toAvaliacaoResponseDTO(Avaliacao avaliacao) {
        if (avaliacao == null) {
            return null;
        }

        return new AvaliacaoResponseDTO(
                avaliacao.getId(),
                avaliacao.getPartida().getId(),
                avaliacao.getJogador().getPessoa().getNome(),
                avaliacao.getAvaliador().getNome(),
                avaliacao.getNota(),
                avaliacao.getComentarios(),
                avaliacao.getDataAvaliacao()
        );
    }

    public List<AvaliacaoResponseDTO> toAvaliacaoResponseDTOList(List<Avaliacao> avaliacoes) {
        return avaliacoes.stream()
                .map(this::toAvaliacaoResponseDTO)
                .collect(Collectors.toList());
    }
}