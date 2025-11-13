package com.kickoff.api.service;

import com.kickoff.api.dto.match.PartidaInputDTO;
import com.kickoff.api.dto.match.PartidaResponseDTO;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.match.PartidaStatus;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.match.PartidaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartidaService {

    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private EquipeRepository equipeRepository;

    @Transactional
    public Partida agendarAmistoso(PartidaInputDTO dto) {
        if (dto.equipeCasaId().equals(dto.equipeVisitanteId())) {
            throw new IllegalArgumentException("Uma equipe não pode jogar contra si mesma.");
        }

        Equipe casa = equipeRepository.findById(dto.equipeCasaId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe da casa não encontrada"));

        Equipe visitante = equipeRepository.findById(dto.equipeVisitanteId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe visitante não encontrada"));

        Partida partida = new Partida();
        partida.setEquipeCasa(casa);
        partida.setEquipeVisitante(visitante);
        partida.setDataHora(dto.dataHora());
        partida.setLocal(dto.local());
        partida.setStatus(PartidaStatus.AGENDADA);
        partida.setPlacarCasa(0);
        partida.setPlacarVisitante(0);
        return partidaRepository.save(partida);
    }

    public List<PartidaResponseDTO> listarPartidasDoTime(Long equipeId) {
        return partidaRepository.findPartidasPorEquipe(equipeId).stream()
                .map(p -> new PartidaResponseDTO(
                        p.getId(),
                        p.getDataHora(),
                        p.getLocal(),
                        p.getEquipeCasa().getNome(),
                        p.getEquipeVisitante().getNome(),
                        p.getPlacarCasa(),
                        p.getPlacarVisitante(),
                        p.getStatus(),
                        p.getEquipeCasa().getId(),
                        p.getEquipeVisitante().getId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void atualizarStatus(Long id, String statusStr) {
        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));
        try {
            PartidaStatus status = PartidaStatus.valueOf(statusStr);
            partida.setStatus(status);
            partidaRepository.save(partida);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + statusStr);
        }
    }

    public PartidaResponseDTO buscarPorId(Long id) {
        Partida p = partidaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));

        return new PartidaResponseDTO(
                p.getId(),
                p.getDataHora(),
                p.getLocal(),
                p.getEquipeCasa().getNome(),
                p.getEquipeVisitante().getNome(),
                p.getPlacarCasa(),
                p.getPlacarVisitante(),
                p.getStatus(),
                p.getEquipeCasa().getId(),
                p.getEquipeVisitante().getId()
        );
    }
}