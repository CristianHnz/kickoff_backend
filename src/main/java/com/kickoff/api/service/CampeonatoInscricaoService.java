package com.kickoff.api.service;

import com.kickoff.api.dto.match.CampeonatoEquipesRequest;
import com.kickoff.api.dto.match.CampeonatoEquipeDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class CampeonatoInscricaoService {

    @Autowired private CampeonatoRepository campeonatoRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private CampeonatoEquipeRepository campeonatoEquipeRepository;
    @Autowired private PartidaRepository partidaRepository;

    @Transactional
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public void adicionarEquipes(Long campeonatoId, CampeonatoEquipesRequest req) {
        Campeonato camp = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado."));

        for (Long equipeId : req.equipeIds()) {
            Equipe eq = equipeRepository.findById(equipeId)
                    .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada: " + equipeId));
            campeonatoEquipeRepository.findByCampeonatoAndEquipe(camp, eq)
                    .orElseGet(() -> campeonatoEquipeRepository.save(new CampeonatoEquipe(null, camp, eq)));
        }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public List<CampeonatoEquipeDTO> listarEquipes(Long campeonatoId) {
        Campeonato camp = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado."));
        return campeonatoEquipeRepository.findByCampeonato(camp).stream()
                .map(ce -> new CampeonatoEquipeDTO(ce.getEquipe().getId(), ce.getEquipe().getNome()))
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public void removerEquipe(Long campeonatoId, Long equipeId) {
        Campeonato camp = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado."));
        Equipe eq = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada."));
        campeonatoEquipeRepository.deleteByCampeonatoAndEquipe(camp, eq);
    }

    @Transactional
    @PreAuthorize("hasRole('GESTOR_EQUIPE')")
    public List<Partida> gerarPartidasTurnoUnico(Long campeonatoId) {
        Campeonato camp = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado."));

        List<Equipe> equipes = campeonatoEquipeRepository.findByCampeonato(camp).stream()
                .map(CampeonatoEquipe::getEquipe).toList();

        if (equipes.size() < 2) {
            throw new IllegalArgumentException("É necessário ao menos 2 equipes inscritas para gerar partidas.");
        }

        List<Long> ids = new ArrayList<>(equipes.stream().map(Equipe::getId).toList());
        boolean hadBye = false;
        if (ids.size() % 2 == 1) {
            ids.add(-1L); // BYE
            hadBye = true;
        }

        int n = ids.size();
        int rodadas = n - 1;
        int jogosPorRodada = n / 2;

        LocalDate dIni = camp.getDataInicio();
        LocalDate dFim = camp.getDataFim();
        LocalDate dataRodada = dIni;
        LocalTime hora = LocalTime.of(10, 0);

        List<Partida> criadas = new ArrayList<>();

        for (int r = 0; r < rodadas; r++) {
            if (dataRodada.isAfter(dFim)) {
                throw new IllegalArgumentException("Período do campeonato é insuficiente para todas as rodadas.");
            }

            for (int j = 0; j < jogosPorRodada; j++) {
                Long casaId = ids.get(j);
                Long foraId = ids.get(n - 1 - j);
                if (Objects.equals(casaId, -1L) || Objects.equals(foraId, -1L)) continue; // ignora BYE

                Equipe casa = equipeRepository.findById(casaId)
                        .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada: " + casaId));
                Equipe fora = equipeRepository.findById(foraId)
                        .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada: " + foraId));

                Partida p = new Partida();
                p.setCampeonato(camp);
                p.setEquipeCasa(casa);
                p.setEquipeVisitante(fora);
                p.setArbitro(null);
                p.setLocal(null);
                p.setDataHora(LocalDateTime.of(dataRodada, hora));
                p.setPlacarCasa(0);
                p.setPlacarVisitante(0);
                p.setStatus(PartidaStatus.AGENDADA);
                criadas.add(partidaRepository.save(p));
            }

            List<Long> mid = new ArrayList<>(ids.subList(1, n));
            Collections.rotate(mid, 1);
            ids = new ArrayList<>(List.of(ids.get(0)));
            ids.addAll(mid);

            dataRodada = dataRodada.plusDays(1);
        }

        if (hadBye) {
        }

        return criadas;
    }
}
