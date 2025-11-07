package com.kickoff.api.dto.role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ComissaoTecnicaDTO(

        @NotBlank(message = "Email da pessoa é obrigatório")
        @Email(message = "Email inválido")
        String emailPessoa,

        @NotBlank(message = "Função é obrigatória")
        String funcao
) {}