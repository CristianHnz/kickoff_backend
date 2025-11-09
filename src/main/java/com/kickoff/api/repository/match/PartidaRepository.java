package com.kickoff.api.repository.match;

import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.model.match.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, Long> {

    List<Partida> findByCampeonato(Campeonato campeonato);
    @Query("""
           select (count(p) > 0) from Partida p
           where p.dataHora = :data
             and (p.equipeCasa.id = :equipeId or p.equipeVisitante.id = :equipeId)
           """)
    boolean existsConflitoHorario(@Param("data") LocalDateTime data,
                                  @Param("equipeId") Long equipeId);
    @Query("""
           select (count(p) > 0) from Partida p
           where p.dataHora = :data
             and ((p.equipeCasa.id = :casa and p.equipeVisitante.id = :fora)
               or  (p.equipeCasa.id = :fora and p.equipeVisitante.id = :casa))
           """)
    boolean existsMesmosTimesMesmoHorario(@Param("data") LocalDateTime data,
                                          @Param("casa") Long equipeCasaId,
                                          @Param("fora") Long equipeVisitanteId);
}
