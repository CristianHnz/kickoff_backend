package com.kickoff.api.repository.lookup;

import com.kickoff.api.model.lookup.Posicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosicaoRepository extends JpaRepository<Posicao, Long> {
}