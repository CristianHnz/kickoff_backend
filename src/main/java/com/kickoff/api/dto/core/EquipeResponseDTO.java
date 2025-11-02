package com.kickoff.api.dto.core;

import java.time.LocalDateTime;

public record EquipeResponseDTO(
        Long id,
        String nome,
        String cidade,
        String estado,
        LocalDateTime dataCriacao,
        AdministradorDTO administrador
) {}