package com.kickoff.api.dto;

public record AuthResponseDTO(
        Long id,
        String nome,
        String email,
        String role
) {}