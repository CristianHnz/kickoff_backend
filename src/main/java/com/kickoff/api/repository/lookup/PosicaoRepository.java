package com.kickoff.api.repository.lookup;

import com.kickoff.api.model.lookup.Posicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PosicaoRepository extends JpaRepository<Posicao, Long> {
    Optional<Posicao> findByDescricao(String descricao);
}