package com.kickoff.api.service;

import com.kickoff.api.dto.auth.MeuPerfilDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.lookup.Posicao;
import com.kickoff.api.model.role.Arbitro;
import com.kickoff.api.model.role.ComissaoTecnica;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.auth.UsuarioRepository;
import com.kickoff.api.repository.core.PessoaRepository;
import com.kickoff.api.repository.lookup.PosicaoRepository;
import com.kickoff.api.repository.role.ArbitroRepository;
import com.kickoff.api.repository.role.ComissaoTecnicaRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PerfilService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private JogadorRepository jogadorRepository;
    @Autowired private ArbitroRepository arbitroRepository;
    @Autowired private PosicaoRepository posicaoRepository;

    public MeuPerfilDTO getMeuPerfil(String email) {
        Pessoa pessoa = pessoaRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));

        String tipoPessoa = pessoa.getTipoPessoa().getDescricao();
        List<String> posicoes = null;
        String licencaCbf = null;

        if ("JOGADOR".equals(tipoPessoa)) {
            posicoes = jogadorRepository.findByPessoaId(pessoa.getId())
                    .map(jogador -> jogador.getPosicoes().stream()
                            .map(Posicao::getDescricao)
                            .collect(Collectors.toList()))
                    .orElse(List.of());
        } else if ("ARBITRO".equals(tipoPessoa)) {
            licencaCbf = arbitroRepository.findByPessoaId(pessoa.getId())
                    .map(Arbitro::getLicencaCbf)
                    .orElse(null);
        }

        return new MeuPerfilDTO(
                pessoa.getNome(),
                pessoa.getEmail(),
                pessoa.getCpf(),
                pessoa.getTelefone(),
                pessoa.getDataNascimento(),
                tipoPessoa,
                posicoes,
                licencaCbf
        );
    }

    @Transactional
    public void updateMeuPerfil(String email, MeuPerfilDTO dto) {
        Pessoa pessoa = pessoaRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));

        pessoa.setTelefone(dto.telefone());
        pessoa.setDataNascimento(dto.dataNascimento());
        pessoa.setNome(dto.nome());
        pessoaRepository.save(pessoa);

        String tipoPessoa = pessoa.getTipoPessoa().getDescricao();

        if ("JOGADOR".equals(tipoPessoa) && dto.posicoes() != null) {
            Jogador jogador = jogadorRepository.findByPessoaId(pessoa.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Registro de Jogador não encontrado"));

            Set<Posicao> posicoesSet = dto.posicoes().stream()
                    .map(nomePos -> posicaoRepository.findByDescricao(nomePos)
                            .orElseThrow(() -> new EntityNotFoundException("Posição: " + nomePos + " não existe")))
                    .collect(Collectors.toSet());

            jogador.setPosicoes(posicoesSet);
            jogadorRepository.save(jogador);
        } else if ("ARBITRO".equals(tipoPessoa)) {
            Arbitro arbitro = arbitroRepository.findByPessoaId(pessoa.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Registro de Árbitro não encontrado"));
            arbitro.setLicencaCbf(dto.licencaCbf());
            arbitroRepository.save(arbitro);
        }
    }
}