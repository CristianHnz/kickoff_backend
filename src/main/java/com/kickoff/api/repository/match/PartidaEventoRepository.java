package com.kickoff.api.repository.match;

import com.kickoff.api.model.match.PartidaEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaEventoRepository extends JpaRepository<PartidaEvento, Long> {
}