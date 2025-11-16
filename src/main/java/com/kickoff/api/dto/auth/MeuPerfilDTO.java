package com.kickoff.api.dto.auth;

import java.time.LocalDate;
import java.util.List;

public record MeuPerfilDTO(
        String nome,
        String email,
        String cpf,
        String telefone,
        LocalDate dataNascimento,

        String tipoPessoa,

        List<String> posicoes,
        String licencaCbf
) {}