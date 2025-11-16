package com.kickoff.api.dto.role;

public record StaffResumoDTO(
        Long id,
        String nome,
        String funcao,
        String status
) {}