package com.kickoff.api.repository.role;

import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.role.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JogadorRepository extends JpaRepository<Jogador, Long> {

    Optional<Jogador> findByPessoa(Pessoa pessoa);
    List<Jogador> findByEquipe(Equipe equipe);
    @Query("SELECT j FROM Jogador j JOIN FETCH j.posicoes WHERE j.id = :id")
    Optional<Jogador> findByIdWithPosicoes(@Param("id") Long id);
}
