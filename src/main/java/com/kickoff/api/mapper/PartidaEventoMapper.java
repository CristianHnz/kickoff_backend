package com.kickoff.api.mapper;

import com.kickoff.api.dto.match.PartidaEventoResponseDTO;
import com.kickoff.api.model.match.PartidaEvento;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PartidaEventoMapper {

    public PartidaEventoResponseDTO toPartidaEventoResponseDTO(PartidaEvento evento) {
        if (evento == null) {
            return null;
        }

        String nomeJogador = (evento.getJogador() != null)
                ? evento.getJogador().getPessoa().getNome()
                : null;

        return new PartidaEventoResponseDTO(
                evento.getId(),
                evento.getPartida().getId(),
                nomeJogador,
                evento.getEquipe().getNome(),
                evento.getTipoEvento(),
                evento.getMinuto(),
                evento.getDescricao(),
                evento.getDataHoraRegistro()
        );
    }

    public List<PartidaEventoResponseDTO> toPartidaEventoResponseDTOList(List<PartidaEvento> eventos) {
        return eventos.stream()
                .map(this::toPartidaEventoResponseDTO)
                .collect(Collectors.toList());
    }
}