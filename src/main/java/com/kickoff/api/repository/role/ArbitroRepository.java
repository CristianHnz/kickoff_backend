package com.kickoff.api.repository.role;

import com.kickoff.api.model.role.Arbitro;
import com.kickoff.api.model.role.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArbitroRepository extends JpaRepository<Arbitro, Long> {
    Optional<Arbitro> findByPessoaId(Long pessoaId);
}