package com.kickoff.api.repository.match;

import com.kickoff.api.model.match.Campeonato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampeonatoRepository extends JpaRepository<Campeonato, Long> {
    Optional<Campeonato> findByNomeAndAno(String nome, Integer anoR);
}