package com.kickoff.api.service;

import com.kickoff.api.dto.role.JogadorDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.lookup.Posicao;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.core.PessoaRepository;
import com.kickoff.api.repository.lookup.PosicaoRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class JogadorService {

    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private PessoaRepository pessoaRepository;
    @Autowired
    private JogadorRepository jogadorRepository;
    @Autowired
    private PosicaoRepository posicaoRepository;

    @Transactional
    public Jogador adicionarJogador(Long equipeId, JogadorDTO dto, Usuario administrador) {

        Equipe equipe = equipeRepository.findByIdAndAdministrador(equipeId, administrador)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada ou você não tem permissão sobre ela."));

        Pessoa pessoaJogador = pessoaRepository.findByEmail(dto.emailJogador())
                .orElseThrow(() -> new EntityNotFoundException("Nenhum jogador encontrado com o email: " + dto.emailJogador()));

        if (!pessoaJogador.getTipoPessoa().getDescricao().equals("JOGADOR")) {
            throw new IllegalArgumentException("Esta pessoa não está cadastrada como JOGADOR.");
        }

        jogadorRepository.findByPessoa(pessoaJogador).ifPresent(j -> {
            throw new IllegalArgumentException("Este jogador já está alocado na equipe: " + j.getEquipe().getNome());
        });

        Set<Posicao> posicoes = new HashSet<>(posicaoRepository.findAllById(dto.posicoesIds()));
        if (posicoes.size() != dto.posicoesIds().size()) {
            throw new EntityNotFoundException("Uma ou mais posições não foram encontradas.");
        }

        Jogador novoJogador = new Jogador();
        novoJogador.setPessoa(pessoaJogador);
        novoJogador.setEquipe(equipe);
        novoJogador.setNumeroCamisa(dto.numeroCamisa());
        novoJogador.setPosicoes(posicoes);

        return jogadorRepository.save(novoJogador);
    }

    @Transactional(readOnly = true)
    public List<Jogador> listarJogadoresPorEquipe(Long equipeId, Usuario administrador) {
        Equipe equipe = equipeRepository.findByIdAndAdministrador(equipeId, administrador)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada ou você não tem permissão sobre ela."));
        return jogadorRepository.findByEquipe(equipe);
    }

    @Transactional(readOnly = true)
    public Jogador getJogadorDetails(Long jogadorId, Usuario administrador) {
        Jogador jogador = jogadorRepository.findByIdWithPosicoes(jogadorId)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo de jogador não encontrado."));

        if (!jogador.getEquipe().getAdministrador().getId().equals(administrador.getId())) {
            throw new AccessDeniedException("Você não tem permissão para ver este jogador.");
        }

        return jogador;
    }

    @Transactional
    public Jogador updateJogador(Long jogadorId, JogadorDTO dto, Usuario administrador) {
        Jogador jogador = jogadorRepository.findById(jogadorId)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo de jogador não encontrado."));

        if (!jogador.getEquipe().getAdministrador().getId().equals(administrador.getId())) {
            throw new AccessDeniedException("Você não tem permissão para editar este jogador.");
        }

        if (!jogador.getPessoa().getEmail().equals(dto.emailJogador())) {
            Pessoa novaPessoaJogador = pessoaRepository.findByEmail(dto.emailJogador())
                    .orElseThrow(() -> new EntityNotFoundException("Nenhum jogador encontrado com o novo email: " + dto.emailJogador()));

            if (!novaPessoaJogador.getTipoPessoa().getDescricao().equals("JOGADOR")) {
                throw new IllegalArgumentException("Esta pessoa não está cadastrada como JOGADOR.");
            }

            Optional<Jogador> vinculoExistente = jogadorRepository.findByPessoa(novaPessoaJogador);
            if (vinculoExistente.isPresent() && !vinculoExistente.get().getId().equals(jogadorId)) {
                throw new IllegalArgumentException("O novo jogador (email) já está alocado em outra equipe.");
            }
            jogador.setPessoa(novaPessoaJogador);
        }

        Set<Posicao> posicoes = new HashSet<>(posicaoRepository.findAllById(dto.posicoesIds()));
        if (posicoes.size() != dto.posicoesIds().size()) {
            throw new EntityNotFoundException("Uma ou mais posições não foram encontradas.");
        }
        jogador.setPosicoes(posicoes);

        jogador.setNumeroCamisa(dto.numeroCamisa());

        return jogadorRepository.save(jogador);
    }

    @Transactional
    public void deleteJogador(Long jogadorId, Usuario administrador) {
        Jogador jogador = jogadorRepository.findById(jogadorId)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo de jogador não encontrado."));
        if (!jogador.getEquipe().getAdministrador().getId().equals(administrador.getId())) {
            throw new AccessDeniedException("Você não tem permissão para excluir este jogador.");
        }
        jogadorRepository.delete(jogador);
    }
}