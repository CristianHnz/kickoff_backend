package com.kickoff.api.service;

import com.kickoff.api.dto.match.PartidaDTO;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.role.Arbitro;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.match.CampeonatoRepository;
import com.kickoff.api.repository.match.PartidaRepository;
import com.kickoff.api.repository.role.ArbitroRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartidaService {

    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private CampeonatoRepository campeonatoRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private ArbitroRepository arbitroRepository;

    @Transactional
    public Partida criarPartida(PartidaDTO dto) {

        Campeonato campeonato = campeonatoRepository.findById(dto.campeonatoId())
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado."));

        Equipe equipeCasa = equipeRepository.findById(dto.equipeCasaId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe da casa não encontrada."));

        Equipe equipeVisitante = equipeRepository.findById(dto.equipeVisitanteId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe visitante não encontrada."));

        Arbitro arbitro = arbitroRepository.findById(dto.arbitroId())
                .orElseThrow(() -> new EntityNotFoundException("Árbitro não encontrado."));

        if (equipeCasa.getId().equals(equipeVisitante.getId())) {
            throw new IllegalArgumentException("Equipe da casa e visitante não podem ser a mesma.");
        }

        LocalDateTime inicioCampeonato = campeonato.getDataInicio().atStartOfDay();
        LocalDateTime fimCampeonato = campeonato.getDataFim().atTime(23, 59, 59);
        if (dto.dataHora().isBefore(inicioCampeonato) || dto.dataHora().isAfter(fimCampeonato)) {
            throw new IllegalArgumentException("A data da partida (" + dto.dataHora() +
                    ") está fora do período do campeonato (" + inicioCampeonato + " a " + fimCampeonato + ").");
        }

        Partida novaPartida = new Partida();
        novaPartida.setCampeonato(campeonato);
        novaPartida.setEquipeCasa(equipeCasa);
        novaPartida.setEquipeVisitante(equipeVisitante);
        novaPartida.setArbitro(arbitro);
        novaPartida.setDataHora(dto.dataHora());
        novaPartida.setLocal(dto.local());

        return partidaRepository.save(novaPartida);
    }

    @Transactional(readOnly = true)
    public List<Partida> listarPartidas(Long campeonatoId) {
        if (campeonatoId != null) {
            Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                    .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado."));
            return partidaRepository.findByCampeonato(campeonato);
        } else {
            return partidaRepository.findAll();
        }
    }

    @Transactional(readOnly = true)
    public Partida buscarPartidaPorId(Long id) {
        return partidaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com o ID: " + id));
    }

    @Transactional
    public Partida atualizarPartida(Long id, PartidaDTO dto) {
        Partida partidaExistente = buscarPartidaPorId(id);

        Campeonato campeonato = campeonatoRepository.findById(dto.campeonatoId())
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado."));

        Equipe equipeCasa = equipeRepository.findById(dto.equipeCasaId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe da casa não encontrada."));

        Equipe equipeVisitante = equipeRepository.findById(dto.equipeVisitanteId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe visitante não encontrada."));

        Arbitro arbitro = arbitroRepository.findById(dto.arbitroId())
                .orElseThrow(() -> new EntityNotFoundException("Árbitro não encontrado."));

        if (equipeCasa.getId().equals(equipeVisitante.getId())) {
            throw new IllegalArgumentException("Equipe da casa e visitante não podem ser a mesma.");
        }

        LocalDateTime inicioCampeonato = campeonato.getDataInicio().atStartOfDay();
        LocalDateTime fimCampeonato = campeonato.getDataFim().atTime(23, 59, 59);

        if (dto.dataHora().isBefore(inicioCampeonato) || dto.dataHora().isAfter(fimCampeonato)) {
            throw new IllegalArgumentException("A data da partida está fora do período do campeonato.");
        }

        partidaExistente.setCampeonato(campeonato);
        partidaExistente.setEquipeCasa(equipeCasa);
        partidaExistente.setEquipeVisitante(equipeVisitante);
        partidaExistente.setArbitro(arbitro);
        partidaExistente.setDataHora(dto.dataHora());
        partidaExistente.setLocal(dto.local());

        return partidaRepository.save(partidaExistente);
    }

    @Transactional
    public void deletarPartida(Long id) {
        Partida partida = buscarPartidaPorId(id);
        partidaRepository.delete(partida);
    }
}