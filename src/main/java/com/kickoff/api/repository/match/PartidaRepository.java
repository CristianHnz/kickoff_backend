package com.kickoff.api.repository.match;

import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.model.match.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, Long> {

    @Query("SELECT p FROM Partida p WHERE p.equipeCasa.id = :equipeId OR p.equipeVisitante.id = :equipeId ORDER BY p.dataHora DESC")
    List<Partida> findPartidasPorEquipe(@Param("equipeId") Long equipeId);
}
