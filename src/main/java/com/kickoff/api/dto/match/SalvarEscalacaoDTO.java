package com.kickoff.api.dto.match;
import jakarta.validation.Valid;
import java.util.List;
public record SalvarEscalacaoDTO(
        @Valid List<JogadorEscaladoInputDTO> escalacao
) {}