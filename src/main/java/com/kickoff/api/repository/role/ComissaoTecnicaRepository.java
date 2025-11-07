package com.kickoff.api.repository.role;

import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.role.ComissaoTecnica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ComissaoTecnicaRepository extends JpaRepository<ComissaoTecnica, Long> {
    List<ComissaoTecnica> findByEquipeId(Long equipeId);
    Optional<ComissaoTecnica> findByPessoa(Pessoa pessoa);
}