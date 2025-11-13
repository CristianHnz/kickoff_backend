package com.kickoff.api.repository.auth;

import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByPessoa(Pessoa pessoa);
    @Query("SELECT u FROM Usuario u JOIN FETCH u.pessoa p WHERE p.email = :email")
    Optional<Usuario> findByPessoaEmail(@Param("email") String email);
}