package com.kickoff.api.dto.role;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

public record JogadorCadastroDTO(
        @NotBlank(message = "Nome é obrigatório")
        String nome,
        @NotBlank(message = "Email é obrigatório")
        @Email
        String email,
        @NotBlank(message = "Senha é obrigatória")
        String senha,
        String cpf,
        String telefone,
        LocalDate dataNascimento,
        @NotNull(message = "O número da camisa é obrigatório")
        Integer numeroCamisa,
        @NotEmpty(message = "Informe pelo menos uma posição")
        Set<Long> posicoesIds
) { }
