package com.kickoff.api.repository.role;

import com.kickoff.api.model.role.ComissaoTecnica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComissaoTecnicaRepository extends JpaRepository<ComissaoTecnica, Long> {
}