package com.kickoff.api.repository.match;

import com.kickoff.api.model.match.PartidaJogador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidaJogadorRepository extends JpaRepository<PartidaJogador, Long> {

    List<PartidaJogador> findByPartidaId(Long partidaId);
    List<PartidaJogador> findByPartidaIdAndEquipeId(Long partidaId, Long equipeId);
    void deleteByPartidaIdAndEquipeId(Long partidaId, Long equipeId);
}