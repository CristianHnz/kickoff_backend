package com.kickoff.api.mapper;

import com.kickoff.api.dto.core.AdministradorDTO;
import com.kickoff.api.dto.core.EquipeResponseDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component public class EquipeMapper {

    public EquipeResponseDTO toEquipeResponseDTO(Equipe equipe) {
        if (equipe == null) {
            return null;
        }

        Usuario adminUsuario = equipe.getAdministrador();
        AdministradorDTO adminDTO = new AdministradorDTO(
                adminUsuario.getPessoa().getId(),
                adminUsuario.getPessoa().getNome(),
                adminUsuario.getPessoa().getEmail()
        );

        return new EquipeResponseDTO(
                equipe.getId(),
                equipe.getNome(),
                equipe.getCidade(),
                equipe.getEstado(),
                equipe.getDataCriacao(),
                adminDTO
        );
    }

    public List<EquipeResponseDTO> toEquipeResponseDTOList(List<Equipe> equipes) {
        return equipes.stream()
                .map(this::toEquipeResponseDTO)
                .collect(Collectors.toList());
    }
}