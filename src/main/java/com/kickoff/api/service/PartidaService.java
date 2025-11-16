package com.kickoff.api.service;

import com.kickoff.api.dto.match.PartidaInputDTO;
import com.kickoff.api.dto.match.PartidaResponseDTO;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.model.match.CampeonatoEquipe;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.match.PartidaStatus;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.match.CampeonatoEquipeRepository;
import com.kickoff.api.repository.match.CampeonatoRepository;
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
    @Autowired
    private CampeonatoEquipeRepository campeonatoEquipeRepository;
    @Autowired
    private CampeonatoRepository campeonatoRepository;

    @Transactional
    public Partida agendarPartida(PartidaInputDTO dto) {
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

        if (dto.campeonatoId() != null) {
            Campeonato c = campeonatoRepository.findById(dto.campeonatoId())
                    .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado"));
            partida.setCampeonato(c);
        }

        return partidaRepository.save(partida);
    }

    public List<PartidaResponseDTO> listarPartidasDoTime(Long equipeId) {
        return partidaRepository.findPartidasPorEquipe(equipeId).stream()
                .map(this::mapToResponseDTO)
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

            if (status == PartidaStatus.FINALIZADA && partida.getCampeonato() != null) {
                processarResultadoCampeonato(partida);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + statusStr);
        }
    }

    private void processarResultadoCampeonato(Partida partida) {
        Long campeonatoId = partida.getCampeonato().getId();

        CampeonatoEquipe timeCasaStats = campeonatoEquipeRepository
                .findByCampeonatoIdAndEquipeId(campeonatoId, partida.getEquipeCasa().getId())
                .orElse(null);

        CampeonatoEquipe timeVisitanteStats = campeonatoEquipeRepository
                .findByCampeonatoIdAndEquipeId(campeonatoId, partida.getEquipeVisitante().getId())
                .orElse(null);

        if (timeCasaStats != null && timeVisitanteStats != null) {

            int placarCasa = partida.getPlacarCasa();
            int placarVisitante = partida.getPlacarVisitante();

            timeCasaStats.setGolsPro(timeCasaStats.getGolsPro() + placarCasa);
            timeCasaStats.setGolsContra(timeCasaStats.getGolsContra() + placarVisitante);

            timeVisitanteStats.setGolsPro(timeVisitanteStats.getGolsPro() + placarVisitante);
            timeVisitanteStats.setGolsContra(timeVisitanteStats.getGolsContra() + placarCasa);

            if (placarCasa > placarVisitante) {
                timeCasaStats.setPontos(timeCasaStats.getPontos() + 3);
                timeCasaStats.setVitorias(timeCasaStats.getVitorias() + 1);
                timeVisitanteStats.setDerrotas(timeVisitanteStats.getDerrotas() + 1);
            } else if (placarVisitante > placarCasa) {
                timeVisitanteStats.setPontos(timeVisitanteStats.getPontos() + 3);
                timeVisitanteStats.setVitorias(timeVisitanteStats.getVitorias() + 1);
                timeCasaStats.setDerrotas(timeCasaStats.getDerrotas() + 1);
            } else {
                timeCasaStats.setPontos(timeCasaStats.getPontos() + 1);
                timeCasaStats.setEmpates(timeCasaStats.getEmpates() + 1);
                timeVisitanteStats.setPontos(timeVisitanteStats.getPontos() + 1);
                timeVisitanteStats.setEmpates(timeVisitanteStats.getEmpates() + 1);
            }

            campeonatoEquipeRepository.save(timeCasaStats);
            campeonatoEquipeRepository.save(timeVisitanteStats);
        }
    }

    public PartidaResponseDTO buscarPorId(Long id) {
        Partida p = partidaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));

        return mapToResponseDTO(p);
    }

    public PartidaResponseDTO mapToResponseDTO(Partida p) {
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

    @Transactional
    public Partida atualizarPartida(Long partidaId, PartidaInputDTO dto) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));

        if (partida.getStatus() != PartidaStatus.AGENDADA) {
            throw new IllegalArgumentException("Só é permitido alterar partidas com status 'AGENDADA'.");
        }

        if (dto.equipeCasaId().equals(dto.equipeVisitanteId())) {
            throw new IllegalArgumentException("Uma equipe não pode jogar contra si mesma.");
        }

        Equipe casa = equipeRepository.findById(dto.equipeCasaId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe da casa não encontrada"));

        Equipe visitante = equipeRepository.findById(dto.equipeVisitanteId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe visitante não encontrada"));

        partida.setEquipeCasa(casa);
        partida.setEquipeVisitante(visitante);
        partida.setDataHora(dto.dataHora());
        partida.setLocal(dto.local());

        if (dto.campeonatoId() != null) {
            Campeonato c = campeonatoRepository.findById(dto.campeonatoId())
                    .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado"));
            partida.setCampeonato(c);
        } else {
            partida.setCampeonato(null);
        }

        return partidaRepository.save(partida);
    }

    @Transactional
    public void cancelarPartida(Long partidaId) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));

        if (partida.getStatus() != PartidaStatus.AGENDADA) {
            throw new IllegalArgumentException("Não é possível cancelar uma partida que já está 'EM ANDAMENTO' ou 'FINALIZADA'.");
        }

        partida.setStatus(PartidaStatus.CANCELADA);
        partidaRepository.save(partida);

        // partidaRepository.delete(partida);
    }
}