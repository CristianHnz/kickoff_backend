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
        Optional<Equipe> findByAdministradorId(Long administradorId);
    List<Equipe> findAllByAdministradorId(Long administradorId);
    }