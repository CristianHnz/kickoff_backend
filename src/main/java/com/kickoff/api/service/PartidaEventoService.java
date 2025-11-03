package com.kickoff.api.service;

import com.kickoff.api.dto.match.PartidaEventoDTO;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.match.PartidaEvento;
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

@Service
public class PartidaEventoService {

    @Autowired
    private PartidaEventoRepository eventoRepository;
    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private JogadorRepository jogadorRepository;
    @Autowired
    private EquipeRepository equipeRepository;

    @Transactional
    public PartidaEvento criarEvento(Long partidaId, PartidaEventoDTO dto) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada."));

        Equipe equipe = equipeRepository.findById(dto.equipeId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada."));

        Jogador jogador = null;
        if (dto.jogadorId() != null) {
            jogador = jogadorRepository.findById(dto.jogadorId())
                    .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado."));

            if (!jogador.getEquipe().getId().equals(equipe.getId())) {
                throw new IllegalArgumentException("O jogador informado não pertence à equipe selecionada.");
            }
        }

        if (!partida.getEquipeCasa().getId().equals(equipe.getId()) && !partida.getEquipeVisitante().getId().equals(equipe.getId())) {
            throw new IllegalArgumentException("A equipe informada não está participando desta partida.");
        }

        PartidaEvento novoEvento = new PartidaEvento();
        novoEvento.setPartida(partida);
        novoEvento.setEquipe(equipe);
        novoEvento.setJogador(jogador); // Pode ser nulo
        novoEvento.setTipoEvento(dto.tipoEvento());
        novoEvento.setMinuto(dto.minuto());
        novoEvento.setDescricao(dto.descricao());

        return eventoRepository.save(novoEvento);
    }

    @Transactional(readOnly = true)
    public List<PartidaEvento> listarEventosPorPartida(Long partidaId) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada."));

        return eventoRepository.findByPartidaOrderByMinutoAsc(partida);
    }
}