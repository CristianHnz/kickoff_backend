package com.kickoff.api.dto.role;

public record ComissaoTecnicaResponseDTO(
        Long id,
        Long pessoaId,
        String nome,
        String funcao
) {}