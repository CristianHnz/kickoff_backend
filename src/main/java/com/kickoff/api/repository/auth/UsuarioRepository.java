package com.kickoff.api.repository.auth;

import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Essencial para o Spring Security encontrar o usuário pelo "username" (nosso email)
    Optional<Usuario> findByPessoa(Pessoa pessoa);

    Optional<Usuario> findByPessoaEmail(String email);
}