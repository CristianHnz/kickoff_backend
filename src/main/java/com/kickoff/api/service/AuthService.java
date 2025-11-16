package com.kickoff.api.service;

import com.kickoff.api.dto.AuthCadastroDTO;
import com.kickoff.api.dto.AuthResponseDTO;
import com.kickoff.api.dto.auth.AlterarSenhaDTO;
import com.kickoff.api.dto.core.GestorCadastroDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.lookup.Posicao;
import com.kickoff.api.model.lookup.TipoPessoa;
import com.kickoff.api.model.role.Arbitro;
import com.kickoff.api.model.role.ComissaoTecnica;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.auth.UsuarioRepository;
import com.kickoff.api.repository.core.PessoaRepository;
import com.kickoff.api.repository.lookup.PosicaoRepository;
import com.kickoff.api.repository.lookup.TipoPessoaRepository;
import com.kickoff.api.repository.role.ArbitroRepository;
import com.kickoff.api.repository.role.ComissaoTecnicaRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private PessoaRepository pessoaRepository;
    @Autowired
    private TipoPessoaRepository tipoPessoaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PosicaoRepository posicaoRepository;
    @Autowired
    private JogadorRepository jogadorRepository;
    @Autowired
    private ArbitroRepository arbitroRepository;
    @Autowired
    private ComissaoTecnicaRepository comissaoTecnicaRepository;

    @Transactional
    public AuthResponseDTO registrar(AuthCadastroDTO dto) {
        if (pessoaRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Email já está em uso.");
        }

        TipoPessoa tipoPessoa = tipoPessoaRepository.findByDescricao(dto.tipoPessoa().toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de pessoa não encontrado: " + dto.tipoPessoa()));

        String encodedPassword = passwordEncoder.encode(dto.senha());

        Pessoa novaPessoa = new Pessoa();
        novaPessoa.setNome(dto.nome());
        novaPessoa.setEmail(dto.email());
        novaPessoa.setTipoPessoa(tipoPessoa);
        novaPessoa.setCpf(dto.cpf());
        novaPessoa.setTelefone(dto.telefone());
        novaPessoa.setDataNascimento(dto.dataNascimento());

        String role = mapTipoToRole(tipoPessoa.getDescricao());
        Usuario novoUsuario = new Usuario();
        novoUsuario.setPassword(encodedPassword);
        novoUsuario.setRole(role);

        novaPessoa.setUsuario(novoUsuario);
        novoUsuario.setPessoa(novaPessoa);

        Pessoa pessoaSalva = pessoaRepository.save(novaPessoa);

        criarEntidadeVinculada(pessoaSalva, tipoPessoa.getDescricao(), dto);

        return new AuthResponseDTO(
                pessoaSalva.getId(),
                pessoaSalva.getNome(),
                pessoaSalva.getEmail(),
                pessoaSalva.getUsuario().getRole()
        );
    }

    @Transactional
    public AuthResponseDTO registrarPeloGestor(GestorCadastroDTO dto) {
        if (pessoaRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Este email já está em uso.");
        }
        if (pessoaRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new IllegalArgumentException("Este CPF já está em uso.");
        }

        TipoPessoa tipoPessoa = tipoPessoaRepository.findByDescricao(dto.tipoPessoa().toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Pessoa '" + dto.tipoPessoa() + "' não encontrado."));

        Pessoa novaPessoa = new Pessoa();
        novaPessoa.setNome(dto.nome());
        novaPessoa.setEmail(dto.email());
        novaPessoa.setCpf(dto.cpf());
        novaPessoa.setTelefone(dto.telefone());
        novaPessoa.setDataNascimento(dto.dataNascimento());
        novaPessoa.setTipoPessoa(tipoPessoa);

        String role = mapTipoToRole(tipoPessoa.getDescricao());
        String encodedPassword = passwordEncoder.encode(dto.senha());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setRole(role);
        novoUsuario.setPassword(encodedPassword);

        novaPessoa.setUsuario(novoUsuario);
        novoUsuario.setPessoa(novaPessoa);

        Pessoa pessoaSalva = pessoaRepository.save(novaPessoa);

        criarEntidadeVinculada(pessoaSalva, tipoPessoa.getDescricao(), dto);

        return new AuthResponseDTO(
                pessoaSalva.getId(),
                pessoaSalva.getNome(),
                pessoaSalva.getEmail(),
                novoUsuario.getRole()
        );
    }

    private void criarEntidadeVinculada(Pessoa pessoa, String tipoDescricao, GestorCadastroDTO dto) {
        switch (tipoDescricao) {
            case "JOGADOR" -> {
                Jogador novoJogador = new Jogador();
                novoJogador.setPessoa(pessoa);

                if (dto.posicoes() != null && !dto.posicoes().isEmpty()) {
                    java.util.Set<Posicao> posicoesSet = new java.util.HashSet<>();

                    for (String nomePosicao : dto.posicoes()) {
                        Posicao pos = posicaoRepository.findByDescricao(nomePosicao)
                                .orElseThrow(() -> new EntityNotFoundException("Posição não encontrada: " + nomePosicao));
                        posicoesSet.add(pos);
                    }

                    novoJogador.setPosicoes(posicoesSet);
                }

                jogadorRepository.save(novoJogador);
            }

            case "TECNICO", "AUXILIAR" -> {
                ComissaoTecnica novaComissao = new ComissaoTecnica();
                novaComissao.setPessoa(pessoa);
                novaComissao.setFuncao(tipoDescricao);
                comissaoTecnicaRepository.save(novaComissao);
            }

            default -> {
            }
        }
    }

    private void criarEntidadeVinculada(Pessoa pessoa, String tipoDescricao, AuthCadastroDTO dto) {
        switch (tipoDescricao) {
            case "JOGADOR" -> {
                Jogador novoJogador = new Jogador();
                novoJogador.setPessoa(pessoa);

                if (dto.posicoes() != null && !dto.posicoes().isEmpty()) {
                    java.util.Set<Posicao> posicoesSet = new java.util.HashSet<>();
                    for (String nomePosicao : dto.posicoes()) {
                        Posicao pos = posicaoRepository.findByDescricao(nomePosicao)
                                .orElseThrow(() -> new EntityNotFoundException("Posição não encontrada: " + nomePosicao));
                        posicoesSet.add(pos);
                    }
                    novoJogador.setPosicoes(posicoesSet);
                }

                jogadorRepository.save(novoJogador);
            }
            case "ARBITRO" -> {
                Arbitro novoArbitro = new Arbitro();
                novoArbitro.setPessoa(pessoa);
                novoArbitro.setLicencaCbf(dto.licencaCbf());
                arbitroRepository.save(novoArbitro);
            }
            case "TECNICO", "AUXILIAR" -> {
                ComissaoTecnica novaComissao = new ComissaoTecnica();
                novaComissao.setPessoa(pessoa);
                novaComissao.setFuncao(tipoDescricao);
                comissaoTecnicaRepository.save(novaComissao);
            }
            default -> {
            }
        }
    }

    /**
     * Altera a senha de um usuário logado.
     *
     * @param emailUsuarioLogado O email (sub) vindo do token JWT.
     * @param dto                O DTO contendo a senha antiga e a nova.
     */
    @Transactional
    public void alterarSenha(String emailUsuarioLogado, AlterarSenhaDTO dto) {
        Usuario usuario = usuarioRepository.findByPessoaEmail(emailUsuarioLogado)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        if (!passwordEncoder.matches(dto.senhaAntiga(), usuario.getPassword())) {
            throw new IllegalArgumentException("A senha antiga está incorreta.");
        }

        if (passwordEncoder.matches(dto.novaSenha(), usuario.getPassword())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha antiga.");
        }

        String novaSenhaCriptografada = passwordEncoder.encode(dto.novaSenha());
        usuario.setPassword(novaSenhaCriptografada);

        usuarioRepository.save(usuario);
    }

    private String mapTipoToRole(String tipoPessoa) {
        return switch (tipoPessoa) {
            case "JOGADOR" -> "ROLE_JOGADOR";
            case "TECNICO" -> "ROLE_TECNICO";
            case "AUXILIAR" -> "ROLE_AUXILIAR";
            case "ARBITRO" -> "ROLE_ARBITRO";
            case "ADMINISTRADOR DE EQUIPE" -> "ROLE_GESTOR_EQUIPE";
            default -> "ROLE_USUARIO";
        };
    }
}