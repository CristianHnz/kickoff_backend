package com.kickoff.api.service;

import com.kickoff.api.dto.match.AvaliacaoDTO;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.match.Avaliacao;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.match.AvaliacaoRepository;
import com.kickoff.api.repository.match.PartidaRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private JogadorRepository jogadorRepository;

    @Transactional
    public Avaliacao criarAvaliacao(AvaliacaoDTO dto, Pessoa avaliador) {
        Partida partida = partidaRepository.findById(dto.partidaId())
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada."));

        Jogador jogador = jogadorRepository.findById(dto.jogadorId())
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado."));

        if (!"FINALIZADA".equalsIgnoreCase(partida.getStatus().toString())) {
            throw new IllegalArgumentException("A avaliação só pode ser feita após a partida estar 'FINALIZADA'.");
        }

        boolean jogadorParticipou = partida.getEquipeCasa().getId().equals(jogador.getEquipe().getId()) ||
                partida.getEquipeVisitante().getId().equals(jogador.getEquipe().getId());
        if (!jogadorParticipou) {
            throw new IllegalArgumentException("O jogador selecionado não participou desta partida.");
        }

        avaliacaoRepository.findByPartidaAndJogadorAndAvaliador(partida, jogador, avaliador).ifPresent(a -> {
            throw new IllegalArgumentException("Você já avaliou este jogador para esta partida.");
        });

        Avaliacao novaAvaliacao = new Avaliacao();
        novaAvaliacao.setPartida(partida);
        novaAvaliacao.setJogador(jogador);
        novaAvaliacao.setAvaliador(avaliador);
        novaAvaliacao.setNota(dto.nota());
        novaAvaliacao.setComentarios(dto.comentarios());

        return avaliacaoRepository.save(novaAvaliacao);
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> listarAvaliacoes(Long partidaId, Long jogadorId) {
        if (partidaId != null && jogadorId != null) {
            throw new IllegalArgumentException("Filtre por partida OU por jogador, não ambos.");
        }

        if (partidaId != null) {
            Partida partida = partidaRepository.findById(partidaId)
                    .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada."));
            return avaliacaoRepository.findByPartida(partida);
        }

        if (jogadorId != null) {
            Jogador jogador = jogadorRepository.findById(jogadorId)
                    .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado."));
            return avaliacaoRepository.findByJogador(jogador);
        }

        return avaliacaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Avaliacao buscarAvaliacaoPorId(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada com o ID: " + id));
    }

    @Transactional
    public Avaliacao atualizarAvaliacao(Long id, AvaliacaoDTO dto, Pessoa avaliador) {
        Avaliacao avaliacaoExistente = buscarAvaliacaoPorId(id); // Reutiliza o método que já lança 404

        if (!avaliacaoExistente.getAvaliador().getId().equals(avaliador.getId())) {
            throw new AccessDeniedException("Você não tem permissão para editar esta avaliação.");
        }

        Partida partida = partidaRepository.findById(dto.partidaId())
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada."));

        Jogador jogador = jogadorRepository.findById(dto.jogadorId())
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado."));

        if (!"FINALIZADA".equalsIgnoreCase(partida.getStatus().toString())) {
            throw new IllegalArgumentException("A avaliação só pode ser feita após a partida estar 'FINALIZADA'.");
        }
        boolean jogadorParticipou = partida.getEquipeCasa().getId().equals(jogador.getEquipe().getId()) ||
                partida.getEquipeVisitante().getId().equals(jogador.getEquipe().getId());
        if (!jogadorParticipou) {
            throw new IllegalArgumentException("O jogador selecionado não participou desta partida.");
        }

        avaliacaoExistente.setPartida(partida);
        avaliacaoExistente.setJogador(jogador);
        avaliacaoExistente.setNota(dto.nota());
        avaliacaoExistente.setComentarios(dto.comentarios());
        return avaliacaoRepository.save(avaliacaoExistente);
    }

    @Transactional
    public void deletarAvaliacao(Long id, Pessoa avaliador) {
        Avaliacao avaliacaoExistente = buscarAvaliacaoPorId(id);

        if (!avaliacaoExistente.getAvaliador().getId().equals(avaliador.getId())) {
            throw new AccessDeniedException("Você não tem permissão para excluir esta avaliação.");
        }

        avaliacaoRepository.delete(avaliacaoExistente);
    }
}