package com.kickoff.api.repository.core;

import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    boolean existsByNome(String nome);

    //    Optional<Equipe> findByNome(String nome);
    //    List<Equipe> findByAdministrador(Usuario administrador);
    // Para buscar a equipe que pertence a um usuário específico (o gestor logado)
    Optional<Equipe> findByAdministradorId(Long administradorId);
}