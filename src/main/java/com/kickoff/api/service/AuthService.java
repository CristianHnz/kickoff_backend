package com.kickoff.api.service;

import com.kickoff.api.dto.AuthCadastroDTO;
import com.kickoff.api.dto.AuthResponseDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Pessoa;
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
    private PasswordEncoder passwordEncoder;

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
        novoUsuario.setPassword(encodedPassword); // Salva o hash gerado
        novoUsuario.setRole(role);

        novaPessoa.setUsuario(novoUsuario);
        novoUsuario.setPessoa(novaPessoa);

        Pessoa pessoaSalva = pessoaRepository.save(novaPessoa);

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
}