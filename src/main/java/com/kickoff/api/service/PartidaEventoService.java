package com.kickoff.api.service;

import com.kickoff.api.dto.match.PartidaEventoDTO;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.match.PartidaEvento;
import com.kickoff.api.model.match.PartidaEventoTipo;
import com.kickoff.api.model.match.PartidaStatus;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.match.PartidaEventoRepository;
import com.kickoff.api.repository.match.PartidaRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartidaEventoService {

    @Autowired private PartidaRepository partidaRepository;
    @Autowired private PartidaEventoRepository eventoRepository;
    @Autowired private JogadorRepository jogadorRepository;
    @Autowired private EquipeRepository equipeRepository;

    @Transactional
    public void registrarEvento(PartidaEventoDTO dto) {
        Partida partida = partidaRepository.findById(dto.partidaId())
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));

        if (partida.getStatus() == PartidaStatus.AGENDADA) {
        }

        PartidaEvento evento = new PartidaEvento();
        evento.setPartida(partida);

        try {
            evento.setTipoEvento(PartidaEventoTipo.valueOf(dto.tipoEvento()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de evento inválido: " + dto.tipoEvento());
        }

        evento.setMinuto(dto.minuto());
        evento.setDescricao(dto.descricao());

        if (dto.jogadorId() != null) {
            Jogador j = jogadorRepository.findById(dto.jogadorId())
                    .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado"));
            evento.setJogador(j);
        }

        if (dto.equipeId() != null) {
            Equipe e = equipeRepository.findById(dto.equipeId())
                    .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada"));
            evento.setEquipe(e);
        }

        if (dto.jogadorAssistenciaId() != null) {
            evento.setJogadorAssistencia(jogadorRepository.getReferenceById(dto.jogadorAssistenciaId()));
        }
        if (dto.jogadorSubstituidoId() != null) {
            evento.setJogadorSubstituido(jogadorRepository.getReferenceById(dto.jogadorSubstituidoId()));
        }

        eventoRepository.save(evento);

        atualizarPlacar(partida, evento);
    }

    private void atualizarPlacar(Partida partida, PartidaEvento evento) {
        boolean mudouPlacar = false;

        if (evento.getTipoEvento() == PartidaEventoTipo.GOL) {
            if (evento.getEquipe().getId().equals(partida.getEquipeCasa().getId())) {
                partida.setPlacarCasa(partida.getPlacarCasa() + 1);
            } else {
                partida.setPlacarVisitante(partida.getPlacarVisitante() + 1);
            }
            mudouPlacar = true;
        }
        else if (evento.getTipoEvento() == PartidaEventoTipo.GOL_CONTRA) {
            if (evento.getEquipe().getId().equals(partida.getEquipeCasa().getId())) {
                partida.setPlacarVisitante(partida.getPlacarVisitante() + 1);
            } else {
                partida.setPlacarCasa(partida.getPlacarCasa() + 1);
            }
            mudouPlacar = true;
        }

        if (mudouPlacar) {
            partidaRepository.save(partida);
        }
    }

    public List<PartidaEventoDTO> listarEventos(Long partidaId) {
        return eventoRepository.findByPartidaIdOrderByMinutoAsc(partidaId).stream()
                .map(e -> new PartidaEventoDTO(
                        e.getId(),
                        e.getPartida().getId(),
                        e.getJogador() != null ? e.getJogador().getId() : null,
                        e.getEquipe() != null ? e.getEquipe().getId() : null,
                        e.getTipoEvento().name(),
                        e.getMinuto(),
                        e.getDescricao(),
                        e.getJogadorAssistencia() != null ? e.getJogadorAssistencia().getId() : null,
                        e.getJogadorSubstituido() != null ? e.getJogadorSubstituido().getId() : null
                ))
                .collect(Collectors.toList());
    }
}