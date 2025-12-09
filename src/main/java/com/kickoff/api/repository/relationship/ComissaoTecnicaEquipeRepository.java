package com.kickoff.api.repository.relationship;

import com.kickoff.api.model.relationship.ComissaoTecnicaEquipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComissaoTecnicaEquipeRepository extends JpaRepository<ComissaoTecnicaEquipe, Long> {

    @Query("SELECT cte FROM ComissaoTecnicaEquipe cte WHERE cte.equipe.id = :equipeId AND cte.dataSaida IS NULL")
    List<ComissaoTecnicaEquipe> findAtivosByEquipeId(@Param("equipeId") Long equipeId);
    @Query("SELECT cte FROM ComissaoTecnicaEquipe cte WHERE cte.comissaoTecnica.id = :comissaoId AND cte.dataSaida IS NULL")
    Optional<ComissaoTecnicaEquipe> findContratoAtivo(@Param("comissaoId") Long comissaoId);
    List<ComissaoTecnicaEquipe> findByComissaoTecnicaId(Long comissaoId);
}