package com.kickoff.api.repository.relationship;

import com.kickoff.api.model.relationship.JogadorEquipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JogadorEquipeRepository extends JpaRepository<JogadorEquipe, Long> {

    @Query("SELECT je FROM JogadorEquipe je WHERE je.equipe.id = :equipeId AND je.dataSaida IS NULL")
    List<JogadorEquipe> findAtivosByEquipeId(@Param("equipeId") Long equipeId);
    @Query("SELECT je FROM JogadorEquipe je WHERE je.jogador.id = :jogadorId AND je.dataSaida IS NULL")
    Optional<JogadorEquipe> findContratoAtivo(@Param("jogadorId") Long jogadorId);
    List<JogadorEquipe> findByJogadorIdOrderByDataEntradaDesc(Long jogadorId);
}