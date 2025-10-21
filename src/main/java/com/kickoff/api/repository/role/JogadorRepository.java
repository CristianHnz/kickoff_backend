package com.kickoff.api.repository.role;

import com.kickoff.api.model.role.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JogadorRepository extends JpaRepository<Jogador, Long> {
    // Métodos de busca customizados (ex: por equipe) podem ser adicionados aqui no futuro
}