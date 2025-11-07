package com.kickoff.api.dto.role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record JogadorDTO(
        @NotBlank(message = "O email do jogador é obrigatório para encontrá-lo")
        @Email
        String emailJogador,
        @NotNull(message = "O número da camisa é obrigatório")
        Integer numeroCamisa,
        @NotEmpty(message = "O jogador deve ter pelo menos uma posição")
        Set<Long> posicoesIds
) {
}