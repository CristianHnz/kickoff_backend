package com.kickoff.api.repository.match;

import com.kickoff.api.dto.match.ArtilhariaDTO;
import com.kickoff.api.model.match.PartidaEvento;
import com.kickoff.api.model.match.PartidaEventoTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidaEventoRepository extends JpaRepository<PartidaEvento, Long> {
    List<PartidaEvento> findByPartidaIdOrderByMinutoAsc(Long partidaId);
    @Query("""
        SELECT new com.kickoff.api.dto.match.ArtilhariaDTO(
            pe.jogador.id,
            pe.jogador.pessoa.nome,
            pe.equipe.nome,
            COUNT(pe.id)
        )
        FROM PartidaEvento pe
        WHERE pe.partida.campeonato.id = :campeonatoId
          AND pe.tipoEvento = :tipoEvento
        GROUP BY pe.jogador.id, pe.jogador.pessoa.nome, pe.equipe.nome
        ORDER BY COUNT(pe.id) DESC
    """)
    List<ArtilhariaDTO> findArtilhariaDoCampeonato(
            @Param("campeonatoId") Long campeonatoId,
            @Param("tipoEvento") PartidaEventoTipo tipoEvento
    );

    @Query("SELECT COUNT(pe.id) FROM PartidaEvento pe WHERE pe.jogador.id = :jogadorId AND pe.tipoEvento = :tipoGol")
    Long countGolsByJogadorId(@Param("jogadorId") Long jogadorId, @Param("tipoGol") PartidaEventoTipo tipoGol);
}