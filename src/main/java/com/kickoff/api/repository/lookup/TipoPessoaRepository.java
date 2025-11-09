// src/main/java/com/kickoff/api/repository/lookup/TipoPessoaRepository.java
package com.kickoff.api.repository.lookup;

import com.kickoff.api.model.lookup.TipoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoPessoaRepository extends JpaRepository<TipoPessoa, Long> {

    Optional<TipoPessoa> findByDescricao(String descricao);
}