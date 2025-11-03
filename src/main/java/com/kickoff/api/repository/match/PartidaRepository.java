package com.kickoff.api.repository.match;

import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.model.match.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {

    List<Partida> findByCampeonato(Campeonato campeonato);
}