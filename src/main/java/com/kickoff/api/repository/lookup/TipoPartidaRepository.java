package com.kickoff.api.repository.lookup;

import com.kickoff.api.model.lookup.TipoPartida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPartidaRepository extends JpaRepository<TipoPartida, Long> {
}