package com.kickoff.api.service;

import com.kickoff.api.dto.role.JogadorCadastroDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.lookup.Posicao;
import com.kickoff.api.model.lookup.TipoPessoa;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.auth.UsuarioRepository;
import com.kickoff.api.repository.core.PessoaRepository;
import com.kickoff.api.repository.lookup.PosicaoRepository;
import com.kickoff.api.repository.lookup.TipoPessoaRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class JogadorCadastroService {

    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private TipoPessoaRepository tipoPessoaRepository;
    @Autowired private JogadorRepository jogadorRepository;
    @Autowired private PosicaoRepository posicaoRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public Jogador registrarJogadorCompleto(JogadorCadastroDTO dto) {
        pessoaRepository.findByEmail(dto.email())
                .ifPresent(p -> { throw new IllegalArgumentException("Email já está em uso."); });
        if (dto.cpf() != null && !dto.cpf().isBlank()) {
            pessoaRepository.findByCpf(dto.cpf())
                    .ifPresent(p -> { throw new IllegalArgumentException("CPF já está em uso."); });
        }

        TipoPessoa tipoJogador = tipoPessoaRepository.findByDescricao("JOGADOR")
                .orElseThrow(() -> new EntityNotFoundException("Tipo de pessoa 'JOGADOR' não encontrado."));

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.nome());
        pessoa.setEmail(dto.email());
        pessoa.setCpf(dto.cpf());
        pessoa.setTelefone(dto.telefone());
        pessoa.setDataNascimento(dto.dataNascimento());
        pessoa.setTipoPessoa(tipoJogador);

        Usuario usuario = new Usuario();
        usuario.setPessoa(pessoa);
        usuario.setRole("ROLE_JOGADOR");
        usuario.setPassword(passwordEncoder.encode(dto.senha()));

        pessoa.setUsuario(usuario);

        Pessoa pessoaSalva = pessoaRepository.save(pessoa);

        Set<Posicao> posicoes = new HashSet<>(posicaoRepository.findAllById(dto.posicoesIds()));
        if (posicoes.size() != dto.posicoesIds().size()) {
            throw new EntityNotFoundException("Uma ou mais posições não foram encontradas.");
        }

        Jogador jogador = new Jogador();
        jogador.setPessoa(pessoaSalva);
        jogador.setEquipe(null);
        jogador.setNumeroCamisa(dto.numeroCamisa());
        jogador.setPosicoes(posicoes);

        return jogadorRepository.save(jogador);
    }
}
