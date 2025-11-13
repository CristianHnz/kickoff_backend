package com.kickoff.api.service;

import com.kickoff.api.dto.role.JogadorCadastroDTO;
import com.kickoff.api.dto.role.JogadorResumoDTO;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.lookup.Posicao;
import com.kickoff.api.model.lookup.TipoPessoa;
import com.kickoff.api.model.relationship.JogadorEquipe;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.core.PessoaRepository;
import com.kickoff.api.repository.lookup.PosicaoRepository;
import com.kickoff.api.repository.lookup.TipoPessoaRepository;
import com.kickoff.api.repository.relationship.JogadorEquipeRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JogadorService {

    @Autowired
    private JogadorRepository jogadorRepository;
    @Autowired
    private PessoaRepository pessoaRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private JogadorEquipeRepository jogadorEquipeRepository;
    @Autowired
    private PosicaoRepository posicaoRepository;
    @Autowired
    private TipoPessoaRepository tipoPessoaRepository;

    @Transactional
    public void adicionarJogadorAEquipe(Long equipeId, JogadorCadastroDTO dto) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada"));

        Pessoa pessoa = pessoaRepository.findByEmail(dto.email())
                .orElseGet(() -> criarNovaPessoa(dto));

        Jogador jogador = jogadorRepository.findByPessoaId(pessoa.getId())
                .orElseGet(() -> criarNovoPapelJogador(pessoa, dto));

        jogador.setNumeroCamisa(dto.numeroCamisa());

        Posicao posicao = posicaoRepository.findByDescricao(dto.posicao())
                .orElseThrow(() -> new EntityNotFoundException("Posição inválida: " + dto.posicao()));
        jogador.setPosicoes(Set.of(posicao));
        jogadorRepository.save(jogador);

        if (jogadorEquipeRepository.findContratoAtivo(jogador.getId()).isPresent()) {
            throw new IllegalArgumentException("Este jogador já está vinculado a uma equipe ativa.");
        }

        JogadorEquipe vinculo = new JogadorEquipe();
        vinculo.setJogador(jogador);
        vinculo.setEquipe(equipe);
        vinculo.setDataEntrada(LocalDate.now());

        jogadorEquipeRepository.save(vinculo);
    }

    public List<JogadorResumoDTO> listarJogadoresDaEquipe(Long equipeId) {
        List<JogadorEquipe> vinculos = jogadorEquipeRepository.findAtivosByEquipeId(equipeId);

        return vinculos.stream().map(v -> {
            Jogador j = v.getJogador();
            List<String> posicoes = j.getPosicoes().stream()
                    .map(Posicao::getDescricao).collect(Collectors.toList());

            return new JogadorResumoDTO(
                    j.getId(),
                    j.getPessoa().getId(),
                    j.getPessoa().getNome(),
                    j.getNumeroCamisa(),
                    posicoes,
                    "ATIVO"
            );
        }).collect(Collectors.toList());
    }

    private Pessoa criarNovaPessoa(JogadorCadastroDTO dto) {
        TipoPessoa tipoJogador = tipoPessoaRepository.findByDescricao("JOGADOR")
                .orElseThrow(() -> new IllegalStateException("Tipo JOGADOR não cadastrado no banco"));

        Pessoa p = new Pessoa();
        p.setNome(dto.nome());
        p.setEmail(dto.email());
        p.setTelefone(dto.telefone());
        p.setTipoPessoa(tipoJogador);
        return pessoaRepository.save(p);
    }

    private Jogador criarNovoPapelJogador(Pessoa pessoa, JogadorCadastroDTO dto) {
        Jogador j = new Jogador();
        j.setPessoa(pessoa);
        j.setNumeroCamisa(dto.numeroCamisa());
        return jogadorRepository.save(j);
    }
}