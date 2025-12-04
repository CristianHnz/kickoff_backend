package com.kickoff.api.repository.role;

import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.relationship.ComissaoTecnicaEquipe;
import com.kickoff.api.model.role.ComissaoTecnica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ComissaoTecnicaRepository extends JpaRepository<ComissaoTecnica, Long> {
    Optional<ComissaoTecnica> findByPessoa(Pessoa pessoa);
    @Query("""
        SELECT ct FROM ComissaoTecnica ct 
        WHERE ct.id NOT IN (
            SELECT cte.comissaoTecnica.id FROM ComissaoTecnicaEquipe cte WHERE cte.dataSaida IS NULL
        )
    """)
    List<ComissaoTecnica> findComissaoSemContrato();
    Optional<ComissaoTecnica> findByPessoaId(Long pessoaId);
}