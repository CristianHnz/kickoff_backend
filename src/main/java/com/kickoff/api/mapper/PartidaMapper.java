package com.kickoff.api.mapper;

import com.kickoff.api.dto.match.PartidaArbitroDTO;
import com.kickoff.api.dto.match.PartidaCampeonatoDTO;
import com.kickoff.api.dto.match.PartidaEquipeDTO;
import com.kickoff.api.dto.match.PartidaResponseDTO;
import com.kickoff.api.model.match.Partida;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PartidaMapper {

    public PartidaResponseDTO toPartidaResponseDTO(Partida partida) {
        if (partida == null) {
            return null;
        }

        PartidaCampeonatoDTO campeonatoDTO = new PartidaCampeonatoDTO(
                partida.getCampeonato().getId(),
                partida.getCampeonato().getNome()
        );

        PartidaEquipeDTO equipeCasaDTO = new PartidaEquipeDTO(
                partida.getEquipeCasa().getId(),
                partida.getEquipeCasa().getNome()
        );

        PartidaEquipeDTO equipeVisitanteDTO = new PartidaEquipeDTO(
                partida.getEquipeVisitante().getId(),
                partida.getEquipeVisitante().getNome()
        );

        PartidaArbitroDTO arbitroDTO = new PartidaArbitroDTO(
                partida.getArbitro().getId(),
                partida.getArbitro().getPessoa().getNome() // Pega o nome da Pessoa associada
        );

        return new PartidaResponseDTO(
                partida.getId(),
                partida.getDataHora(),
                partida.getLocal(),
                partida.getStatus(),
                partida.getPlacarCasa(),
                partida.getPlacarVisitante(),
                campeonatoDTO,
                equipeCasaDTO,
                equipeVisitanteDTO,
                arbitroDTO
        );
    }

    public List<PartidaResponseDTO> toPartidaResponseDTOList(List<Partida> partidas) {
        return partidas.stream()
                .map(this::toPartidaResponseDTO)
                .collect(Collectors.toList());
    }
}