package com.kickoff.api.mapper;

import com.kickoff.api.dto.role.JogadorResponseDTO;
import com.kickoff.api.model.role.Jogador;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JogadorMapper {

    public JogadorResponseDTO toJogadorResponseDTO(Jogador jogador) {
        if (jogador == null) {
            return null;
        }

//        String nomeEquipe = (jogador.getEquipe() != null)
//                ? jogador.getEquipe().getNome()
//                : null;

        return new JogadorResponseDTO(
                jogador.getId(),
                jogador.getPessoa().getId(),
                jogador.getPessoa().getNome(),
                jogador.getPessoa().getEmail(),
                jogador.getNumeroCamisa(),
                "nomeEquipe",
                jogador.getPosicoes()
        );
    }

    public List<JogadorResponseDTO> toJogadorResponseDTOList(List<Jogador> jogadores) {
        return jogadores.stream()
                .map(this::toJogadorResponseDTO)
                .collect(Collectors.toList());
    }
}
