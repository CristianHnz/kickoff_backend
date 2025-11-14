package com.kickoff.api.service;

import com.kickoff.api.dto.match.AvaliacaoDTO;
import com.kickoff.api.model.match.Avaliacao;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.match.AvaliacaoRepository;
import com.kickoff.api.repository.match.PartidaRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private JogadorRepository jogadorRepository;

    @Transactional
    public void salvarAvaliacao(AvaliacaoDTO dto) {
        Avaliacao avaliacao = avaliacaoRepository.findByPartidaIdAndJogadorId(dto.partidaId(), dto.jogadorId())
                .orElse(new Avaliacao());

        if (avaliacao.getId() == null) {
            Partida p = partidaRepository.findById(dto.partidaId())
                    .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));
            Jogador j = jogadorRepository.findById(dto.jogadorId())
                    .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado"));

            avaliacao.setPartida(p);
            avaliacao.setJogador(j);
        }

        avaliacao.setNota(dto.nota());
        avaliacao.setComentarios(dto.comentarios());

        avaliacaoRepository.save(avaliacao);
    }

    public List<AvaliacaoDTO> listarPorPartida(Long partidaId) {
        return avaliacaoRepository.findByPartidaId(partidaId).stream()
                .map(a -> new AvaliacaoDTO(
                        a.getId(),
                        a.getPartida().getId(),
                        a.getJogador().getId(),
                        a.getJogador().getPessoa().getNome(),
                        a.getNota(),
                        a.getComentarios()
                ))
                .collect(Collectors.toList());
    }
}