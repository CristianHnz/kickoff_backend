package com.kickoff.api.mapper;

import com.kickoff.api.dto.match.PartidaArbitroDTO;
import com.kickoff.api.dto.match.PartidaCampeonatoDTO;
import com.kickoff.api.dto.match.PartidaEquipeDTO;
import com.kickoff.api.dto.match.PartidaResponseDTO;
import com.kickoff.api.model.match.Partida;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PartidaMapper {

    public PartidaResponseDTO toPartidaResponseDTO(Partida p) {
        if (p == null) return null;

        PartidaCampeonatoDTO campeonatoDTO = (p.getCampeonato() != null)
                ? new PartidaCampeonatoDTO(p.getCampeonato().getId(), p.getCampeonato().getNome())
                : null;

        PartidaEquipeDTO equipeCasaDTO = new PartidaEquipeDTO(p.getEquipeCasa().getId(), p.getEquipeCasa().getNome());
        PartidaEquipeDTO equipeVisitanteDTO = new PartidaEquipeDTO(p.getEquipeVisitante().getId(), p.getEquipeVisitante().getNome());

        PartidaArbitroDTO arbitroDTO = (p.getArbitro() != null)
                ? new PartidaArbitroDTO(p.getArbitro().getId(), p.getArbitro().getPessoa().getNome())
                : null;

        return new PartidaResponseDTO(
                p.getId(),
                p.getDataHora(),
                p.getLocal(),
                (p.getStatus() != null ? p.getStatus() : null),
                p.getPlacarCasa(),
                p.getPlacarVisitante(),
                campeonatoDTO,
                equipeCasaDTO,
                equipeVisitanteDTO,
                arbitroDTO
        );
    }

    public List<PartidaResponseDTO> toPartidaResponseDTOList(List<Partida> partidas) {
        return partidas.stream().map(this::toPartidaResponseDTO).toList();
    }
}
