package com.kickoff.api.repository.match;

import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.match.PartidaEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidaEventoRepository extends JpaRepository<PartidaEvento, Long> {
    List<PartidaEvento> findByPartidaIdOrderByMinutoAsc(Long partidaId);
}