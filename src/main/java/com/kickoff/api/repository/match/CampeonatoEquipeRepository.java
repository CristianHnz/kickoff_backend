package com.kickoff.api.repository.match;

import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.model.match.CampeonatoEquipe;
import com.kickoff.api.model.core.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CampeonatoEquipeRepository extends JpaRepository<CampeonatoEquipe, Long> {
    List<CampeonatoEquipe> findByCampeonato(Campeonato campeonato);
    Optional<CampeonatoEquipe> findByCampeonatoAndEquipe(Campeonato c, Equipe e);
    void deleteByCampeonatoAndEquipe(Campeonato c, Equipe e);
}
