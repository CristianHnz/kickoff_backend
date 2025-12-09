package com.kickoff.api.repository.match;

import com.kickoff.api.model.match.CampeonatoEquipe;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampeonatoEquipeRepository extends JpaRepository<CampeonatoEquipe, Long> {

    List<CampeonatoEquipe> findByCampeonatoId(Long campeonatoId, Sort sort);
    boolean existsByCampeonatoIdAndEquipeId(Long campeonatoId, Long equipeId);
    Optional<CampeonatoEquipe> findByCampeonatoIdAndEquipeId(Long campeonatoId, Long equipeId);
    List<CampeonatoEquipe> findByEquipeId(Long equipeId);
}