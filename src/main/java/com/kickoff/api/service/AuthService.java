package com.kickoff.api.service;

import com.kickoff.api.dto.AuthCadastroDTO;
import com.kickoff.api.dto.AuthResponseDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.lookup.TipoPessoa;
import com.kickoff.api.repository.auth.UsuarioRepository;
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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoPessoaRepository tipoPessoaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional // Garante que ou tudo é salvo, ou nada é (em caso de erro)
    public AuthResponseDTO registrar(AuthCadastroDTO dto) {

        // 1. Validar se o email já existe
        if (pessoaRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Email já está em uso.");
        }

        // 2. Buscar o TipoPessoa (JOGADOR, TECNICO, etc.)
        TipoPessoa tipoPessoa = tipoPessoaRepository.findByDescricao(dto.tipoPessoa().toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de pessoa não encontrado: " + dto.tipoPessoa()));

        // 3. Criar e salvar a nova Pessoa
        Pessoa novaPessoa = new Pessoa();
        novaPessoa.setNome(dto.nome());
        novaPessoa.setEmail(dto.email());
        novaPessoa.setTipoPessoa(tipoPessoa);
        // Outros campos de Pessoa (cpf, telefone) podem ser nulos por enquanto
        Pessoa pessoaSalva = pessoaRepository.save(novaPessoa);

        // 4. Mapear o TipoPessoa para uma Role de segurança
        String role = mapTipoToRole(tipoPessoa.getDescricao());

        // 5. Criar e salvar o novo Usuário
        Usuario novoUsuario = new Usuario();
        novoUsuario.setPessoa(pessoaSalva);
        novoUsuario.setPassword(passwordEncoder.encode(dto.senha()));
        novoUsuario.setRole(role);

        usuarioRepository.save(novoUsuario);

        // 6. Retornar o DTO de resposta
        return new AuthResponseDTO(pessoaSalva.getId(), pessoaSalva.getNome(), pessoaSalva.getEmail(), role);
    }

    private String mapTipoToRole(String tipoPessoa) {
        // Mapeia a descrição da tabela para uma Role do Spring Security
        // Adicionamos "ROLE_" como prefixo por convenção do Spring Security
        return switch (tipoPessoa) {
            case "JOGADOR" -> "ROLE_JOGADOR";
            case "TECNICO" -> "ROLE_TECNICO";
            case "AUXILIAR" -> "ROLE_AUXILIAR";
            case "ARBITRO" -> "ROLE_ARBITRO";
            case "ADMINISTRADOR" -> "ROLE_ADMIN"; // Supondo que você tenha um tipo 'ADMINISTRADOR'
            case "ADMINISTRADOR DE EQUIPE" -> "ROLE_GESTOR_EQUIPE";
            default -> "ROLE_USUARIO";
        };
    }
}