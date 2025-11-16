package com.kickoff.api.service;

import com.kickoff.api.dto.role.*;
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

    public List<JogadorResumoDTO> listarJogadoresDisponiveis() {
        return jogadorRepository.findJogadoresSemContrato().stream()
                .map(j -> new JogadorResumoDTO(
                        j.getId(),
                        j.getPessoa().getId(),
                        j.getPessoa().getNome() + " (" + j.getPessoa().getEmail() + ")", // Exibição
                        j.getNumeroCamisa(),
                        j.getPosicoes().stream().map(Posicao::getDescricao).toList(),
                        "LIVRE"
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void contratarJogadorExistente(Long equipeId, ContratacaoDTO dto) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada"));

        Jogador jogador = jogadorRepository.findById(dto.jogadorId())
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado"));

        if (jogadorEquipeRepository.findContratoAtivo(jogador.getId()).isPresent()) {
            throw new IllegalArgumentException("Este jogador já possui um contrato ativo.");
        }

        jogador.setNumeroCamisa(dto.numeroCamisa());
        Posicao posicao = posicaoRepository.findByDescricao(dto.posicao())
                .orElseThrow(() -> new EntityNotFoundException("Posição inválida"));
        jogador.setPosicoes(new java.util.HashSet<>(java.util.Set.of(posicao)));
        jogadorRepository.save(jogador);

        JogadorEquipe vinculo = new JogadorEquipe();
        vinculo.setJogador(jogador);
        vinculo.setEquipe(equipe);
        vinculo.setDataEntrada(LocalDate.now());
        jogadorEquipeRepository.save(vinculo);
    }

    public List<JogadorResumoDTO> listarTodosJogadores() {
        List<Jogador> todos = jogadorRepository.findAll();

        return todos.stream().map(j -> {

            String status = jogadorEquipeRepository.findContratoAtivo(j.getId())
                    .isPresent() ? "CONTRATADO" : "LIVRE";

            return new JogadorResumoDTO(
                    j.getId(),
                    j.getPessoa().getId(),
                    j.getPessoa().getNome(),
                    j.getNumeroCamisa(),
                    j.getPosicoes().stream().map(Posicao::getDescricao).collect(Collectors.toList()),
                    status
            );
        }).collect(Collectors.toList());
    }

    public JogadorDetalhesDTO buscarDetalhesJogador(Long jogadorId) {
        Jogador jogador = jogadorRepository.findById(jogadorId)
                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado"));

        Pessoa pessoa = jogador.getPessoa();

        List<String> posicoes = jogador.getPosicoes().stream()
                .map(Posicao::getDescricao)
                .collect(Collectors.toList());

        List<JogadorEquipe> vinculos = jogadorEquipeRepository.findByJogadorIdOrderByDataEntradaDesc(jogadorId);

        EquipeAtualDTO equipeAtual = vinculos.stream()
                .filter(v -> v.getDataSaida() == null)
                .findFirst()
                .map(v -> new EquipeAtualDTO(
                        v.getEquipe().getId(),
                        v.getEquipe().getNome(),
                        jogador.getNumeroCamisa(),
                        v.getDataEntrada()
                ))
                .orElse(null);

        List<HistoricoEquipeDTO> historico = vinculos.stream()
                .filter(v -> v.getDataSaida() != null)
                .map(v -> new HistoricoEquipeDTO(
                        v.getEquipe().getNome(),
                        v.getDataEntrada(),
                        v.getDataSaida()
                ))
                .collect(Collectors.toList());

        return new JogadorDetalhesDTO(
                jogador.getId(),
                pessoa.getNome(),
                pessoa.getEmail(),
                pessoa.getCpf(),
                pessoa.getTelefone(),
                pessoa.getDataNascimento(),
                posicoes,
                equipeAtual,
                historico
        );
    }

    @Transactional
    public void dispensarJogador(Long equipeId, Long jogadorId) {
        if (!jogadorRepository.existsById(jogadorId)) {
            throw new EntityNotFoundException("Jogador não encontrado.");
        }

        JogadorEquipe vinculoAtivo = jogadorEquipeRepository.findContratoAtivo(jogadorId)
                .orElseThrow(() -> new EntityNotFoundException("Este jogador não possui um vínculo ativo."));

        if (!vinculoAtivo.getEquipe().getId().equals(equipeId)) {
            throw new IllegalArgumentException("O jogador não pode ser dispensado por esta equipe, pois pertence a outra.");
        }

        vinculoAtivo.setDataSaida(LocalDate.now());

        jogadorEquipeRepository.save(vinculoAtivo);
        Jogador jogador = vinculoAtivo.getJogador();
        jogador.setNumeroCamisa(null);
        jogadorRepository.save(jogador);
    }
}