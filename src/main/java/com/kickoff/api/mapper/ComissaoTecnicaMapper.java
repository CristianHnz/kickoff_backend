package com.kickoff.api.mapper;

import com.kickoff.api.dto.role.ComissaoTecnicaResponseDTO;
import com.kickoff.api.model.role.ComissaoTecnica;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ComissaoTecnicaMapper {

    public ComissaoTecnicaResponseDTO toResponseDTO(ComissaoTecnica comissao) {
        if (comissao == null) {
            return null;
        }

        if (comissao.getPessoa() == null) {
            return new ComissaoTecnicaResponseDTO(
                    comissao.getId(),
                    null,
                    "Pessoa não associada",
                    comissao.getFuncao()
            );
        }

        return new ComissaoTecnicaResponseDTO(
                comissao.getId(),
                comissao.getPessoa().getId(),
                comissao.getPessoa().getNome(),
                comissao.getFuncao()
        );
    }

    public List<ComissaoTecnicaResponseDTO> toResponseDTOList(List<ComissaoTecnica> comissaoList) {
        if (comissaoList == null) {
            return List.of();
        }

        return comissaoList.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}