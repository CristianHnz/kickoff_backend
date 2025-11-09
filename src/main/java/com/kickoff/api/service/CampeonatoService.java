// src/main/java/com/kickoff/api/service/CampeonatoService.java
package com.kickoff.api.service;

import com.kickoff.api.dto.match.CampeonatoDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CampeonatoService {

    @Autowired
    private CampeonatoRepository campeonatoRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private CampeonatoEquipeRepository campeonatoEquipeRepository;
    @Autowired
    private PartidaRepository partidaRepository;

    @Transactional
    public Campeonato criarCampeonato(CampeonatoDTO dto) {
        campeonatoRepository.findByNomeAndAno(dto.nome(), dto.ano()).ifPresent(c -> {
            throw new IllegalArgumentException("Um campeonato com este nome já existe para este ano.");
        });

        if (dto.dataFim().isBefore(dto.dataInicio())) {
            throw new IllegalArgumentException("A data de fim deve ser após a data de início.");
        }

        Campeonato novoCampeonato = new Campeonato();
        novoCampeonato.setNome(dto.nome());
        novoCampeonato.setAno(dto.ano());
        novoCampeonato.setDataInicio(dto.dataInicio());
        novoCampeonato.setDataFim(dto.dataFim());
        return campeonatoRepository.save(novoCampeonato);
    }

    @Transactional(readOnly = true)
    public List<Campeonato> listarCampeonatos() {
        return campeonatoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Campeonato buscarCampeonatoPorId(Long id) {
        return campeonatoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado com o ID: " + id));
    }

    @Transactional
    public Campeonato atualizarCampeonato(Long id, CampeonatoDTO dto) {
        Campeonato campeonatoExistente = buscarCampeonatoPorId(id); // Reutiliza o método que já lança 404

        campeonatoRepository.findByNomeAndAno(dto.nome(), dto.ano()).ifPresent(c -> {
            if (!c.getId().equals(id)) { // Se o ID encontrado for diferente do que estamos editando
                throw new IllegalArgumentException("Um campeonato com este nome já existe para este ano.");
            }
        });

        if (dto.dataFim().isBefore(dto.dataInicio())) {
            throw new IllegalArgumentException("A data de fim deve ser após a data de início.");
        }

        campeonatoExistente.setNome(dto.nome());
        campeonatoExistente.setAno(dto.ano());
        campeonatoExistente.setDataInicio(dto.dataInicio());
        campeonatoExistente.setDataFim(dto.dataFim());

        return campeonatoRepository.save(campeonatoExistente);
    }

    @Transactional
    public void deletarCampeonato(Long id) {
        Campeonato campeonato = buscarCampeonatoPorId(id);
        campeonatoRepository.delete(campeonato);
    }

    @Transactional
    public void adicionarEquipes(Long campeonatoId, List<Long> equipeIds) {
        Campeonato camp = buscarCampeonatoPorId(campeonatoId);
        if (equipeIds == null || equipeIds.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos uma equipe.");
        }

        for (Long equipeId : equipeIds) {
            Equipe eq = equipeRepository.findById(equipeId)
                    .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada: " + equipeId));

            boolean exists = campeonatoEquipeRepository.findByCampeonatoAndEquipe(camp, eq).isPresent();
            if (!exists) {
                CampeonatoEquipe ce = new CampeonatoEquipe();
                ce.setCampeonato(camp);
                ce.setEquipe(eq);
                campeonatoEquipeRepository.save(ce);
            }
        }
    }

    @Transactional
    public int gerarPartidasTurnoUnico(Long campeonatoId) {
        Campeonato camp = buscarCampeonatoPorId(campeonatoId);
        List<CampeonatoEquipe> inscritos = campeonatoEquipeRepository.findByCampeonato(camp);
        if (inscritos.size() < 2) {
            throw new IllegalArgumentException("É necessário ao menos 2 equipes para gerar partidas.");
        }

        List<Equipe> equipes = inscritos.stream().map(CampeonatoEquipe::getEquipe).toList();
        boolean hadBye = false;
        if (equipes.size() % 2 != 0) {
            hadBye = true;
            equipes = new java.util.ArrayList<>(equipes);
            equipes.add(null);
        }

        int n = equipes.size();
        int rodadas = n - 1;
        int jogosPorRodada = n / 2;

        LocalDate dia = camp.getDataInicio();
        LocalDate finalMax = camp.getDataFim();

        int criadas = 0;

        java.util.List<Equipe> lista = new java.util.ArrayList<>(equipes);
        for (int r = 0; r < rodadas; r++) {
            if (dia.isAfter(finalMax)) {
                throw new IllegalArgumentException("Período insuficiente para acomodar as rodadas.");
            }

            for (int j = 0; j < jogosPorRodada; j++) {
                Equipe casa = lista.get(j);
                Equipe fora = lista.get(n - 1 - j);

                if (casa == null || fora == null) continue;

                LocalDateTime dataHora = LocalDateTime.of(dia, LocalTime.of(10, 0));

                if (partidaRepository.existsMesmosTimesMesmoHorario(dataHora, casa.getId(), fora.getId())) {
                    continue;
                }

                Partida p = new Partida();
                p.setCampeonato(camp);
                p.setEquipeCasa(casa);
                p.setEquipeVisitante(fora);
                p.setDataHora(dataHora);
                p.setLocal("A definir");
                p.setPlacarCasa(0);
                p.setPlacarVisitante(0);
                p.setStatus(PartidaStatus.AGENDADA);
                partidaRepository.save(p);
                criadas++;
            }

            java.util.List<Equipe> next = new java.util.ArrayList<>(n);
            next.add(lista.get(0));
            next.add(lista.get(n - 1));
            for (int k = 1; k < n - 1; k++) next.add(lista.get(k));
            lista = next;

            dia = dia.plusDays(1);
        }

        if (hadBye) {
        }

        return criadas;
    }
}