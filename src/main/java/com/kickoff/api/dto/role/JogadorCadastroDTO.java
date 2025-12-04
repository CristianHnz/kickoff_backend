package com.kickoff.api.dto.role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JogadorCadastroDTO(
        @NotBlank String nome,
        @NotBlank @Email String email,
        String telefone,
        @NotNull Integer numeroCamisa,
        @NotBlank String posicao,
        Double altura,
        Double peso
) {}