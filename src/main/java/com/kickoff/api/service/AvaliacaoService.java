package com.kickoff.api.service;

import com.kickoff.api.dto.match.AvaliacaoDTO;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.match.Avaliacao;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.auth.UsuarioRepository;
import com.kickoff.api.repository.match.AvaliacaoRepository;
import com.kickoff.api.repository.match.PartidaRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvaliacaoService {

    @Autowired private AvaliacaoRepository avaliacaoRepository;
    @Autowired private PartidaRepository partidaRepository;
    @Autowired private JogadorRepository jogadorRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @Transactional
    public void salvarAvaliacao(AvaliacaoDTO dto) {
        Partida partida = partidaRepository.findById(dto.partidaId())
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));

        Jogador jogador = jogadorRepository.findById(dto.jogadorId())
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado"));

        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioLogado = usuarioRepository.findByPessoaEmail(emailLogado)
                .orElseThrow(() -> new EntityNotFoundException("Usuário logado não encontrado"));

        Long gestorId = usuarioLogado.getId();

        boolean isGestorCasa = partida.getEquipeCasa().getAdministrador().getId().equals(gestorId);
        boolean isGestorVisitante = partida.getEquipeVisitante().getAdministrador().getId().equals(gestorId);
        Equipe equipeAtualJogador = jogador.getEquipeAtual();

        if (equipeAtualJogador == null) {
            throw new IllegalArgumentException("Este jogador não possui contrato ativo com nenhuma equipe.");
        }

        boolean podeAvaliar = false;

        if (isGestorCasa && equipeAtualJogador.getId().equals(partida.getEquipeCasa().getId())) {
            podeAvaliar = true;
        }
        else if (isGestorVisitante && equipeAtualJogador.getId().equals(partida.getEquipeVisitante().getId())) {
            podeAvaliar = true;
        }

        if (!podeAvaliar) {
            throw new AccessDeniedException("Permissão negada: Você só pode avaliar jogadores que pertencem à sua equipe nesta partida.");
        }

        Avaliacao avaliacao = avaliacaoRepository.findByPartidaIdAndJogadorId(dto.partidaId(), dto.jogadorId())
                .orElse(new Avaliacao());

        if (avaliacao.getId() == null) {
            avaliacao.setPartida(partida);
            avaliacao.setJogador(jogador);
            avaliacao.setAvaliador(usuarioLogado.getPessoa());
        }

        avaliacao.setNotaTecnica(dto.notaTecnica());
        avaliacao.setNotaTatica(dto.notaTatica());
        avaliacao.setNotaFisica(dto.notaFisica());
        avaliacao.setComentarios(dto.comentarios());

        BigDecimal soma = dto.notaTecnica().add(dto.notaTatica()).add(dto.notaFisica());
        BigDecimal media = soma.divide(BigDecimal.valueOf(3), 1, RoundingMode.HALF_UP);
        avaliacao.setMediaFinal(media);

        avaliacaoRepository.save(avaliacao);
    }

    public List<AvaliacaoDTO> listarPorPartida(Long partidaId) {
        return avaliacaoRepository.findByPartidaId(partidaId).stream()
                .map(a -> new AvaliacaoDTO(
                        a.getId(),
                        a.getPartida().getId(),
                        a.getJogador().getId(),
                        a.getJogador().getPessoa().getNome(),
                        a.getNotaTecnica(),
                        a.getNotaTatica(),
                        a.getNotaFisica(),
                        a.getMediaFinal(), // Retorna a média calculada
                        a.getComentarios()
                ))
                .collect(Collectors.toList());
    }
}