package com.kickoff.api.service;

import com.kickoff.api.dto.AuthCadastroDTO;
import com.kickoff.api.dto.AuthResponseDTO;
import com.kickoff.api.dto.core.GestorCadastroDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.role.Arbitro;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.auth.UsuarioRepository;
import com.kickoff.api.repository.role.ArbitroRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import com.kickoff.api.model.lookup.TipoPessoa;
import com.kickoff.api.repository.core.PessoaRepository;
import com.kickoff.api.repository.lookup.TipoPessoaRepository;
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
    private JogadorRepository jogadorRepository;
    @Autowired
    private ArbitroRepository arbitroRepository;

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

        String role = mapTipoToRole(tipoPessoa.getDescricao());
        Usuario novoUsuario = new Usuario();
        novoUsuario.setPassword(encodedPassword);
        novoUsuario.setRole(role);

        novaPessoa.setUsuario(novoUsuario);
        novoUsuario.setPessoa(novaPessoa);

        Pessoa pessoaSalva = pessoaRepository.save(novaPessoa);

        String tipo = tipoPessoa.getDescricao();

        if (tipo.equals("JOGADOR")) {
            Jogador novoJogador = new Jogador();
            novoJogador.setPessoa(pessoaSalva);
            jogadorRepository.save(novoJogador);

        } else if (tipo.equals("ARBITRO")) {
            Arbitro novoArbitro = new Arbitro();
            novoArbitro.setPessoa(pessoaSalva);
            arbitroRepository.save(novoArbitro);

        } else if (tipo.equals("ADMINISTRADOR DE EQUIPE")) {
        }

        return new AuthResponseDTO(
                pessoaSalva.getId(),
                pessoaSalva.getNome(),
                pessoaSalva.getEmail(),
                pessoaSalva.getUsuario().getRole()
        );
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

    @Transactional
    public AuthResponseDTO registrarPeloGestor(GestorCadastroDTO dto) {
        if (pessoaRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Este email já está em uso.");
        }
        if (dto.cpf() != null && !dto.cpf().isBlank()) {
            if (pessoaRepository.findByCpf(dto.cpf()).isPresent()) {
                throw new IllegalArgumentException("Este CPF já está em uso.");
            }
        }

        TipoPessoa tipoPessoa = tipoPessoaRepository.findByDescricao(dto.tipoPessoa())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Pessoa '" + dto.tipoPessoa() + "' não encontrado."));

        Pessoa novaPessoa = new Pessoa();
        novaPessoa.setNome(dto.nome());
        novaPessoa.setEmail(dto.email());
        novaPessoa.setCpf(dto.cpf()); // <- Campo extra
        novaPessoa.setTelefone(dto.telefone()); // <- Campo extra
        novaPessoa.setDataNascimento(dto.dataNascimento()); // <- Campo extra
        novaPessoa.setTipoPessoa(tipoPessoa);
        Pessoa pessoaSalva = pessoaRepository.save(novaPessoa);

        String role = mapTipoToRole(tipoPessoa.getDescricao());
        Usuario novoUsuario = new Usuario();
        novoUsuario.setPessoa(pessoaSalva);
        novoUsuario.setRole(role);
        novoUsuario.setPassword(passwordEncoder.encode(dto.senha()));
        usuarioRepository.save(novoUsuario);

        return new AuthResponseDTO(
                pessoaSalva.getId(),
                pessoaSalva.getNome(),
                pessoaSalva.getEmail(),
                novoUsuario.getRole()
        );
    }
}