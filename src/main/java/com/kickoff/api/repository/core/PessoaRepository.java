package com.kickoff.api.repository.core;

import com.kickoff.api.model.core.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    Optional<Pessoa> findByEmail(String email);
    Optional<Pessoa> findByCpf(String cpf);
}