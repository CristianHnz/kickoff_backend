package com.kickoff.api.repository.match;

import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.match.Avaliacao;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.role.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Optional<Avaliacao> findByPartidaAndJogadorAndAvaliador(Partida partida, Jogador jogador, Pessoa avaliador);
    List<Avaliacao> findByPartida(Partida partida);
    List<Avaliacao> findByJogador(Jogador jogador);
}