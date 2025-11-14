package com.kickoff.api.repository.match;

import com.kickoff.api.model.match.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByPartidaId(Long partidaId);
    Optional<Avaliacao> findByPartidaIdAndJogadorId(Long partidaId, Long jogadorId);
}