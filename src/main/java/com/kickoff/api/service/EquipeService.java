package com.kickoff.api.service;

import com.kickoff.api.dto.core.EquipeDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.repository.core.EquipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    @Transactional
    public Equipe criarEquipe(EquipeDTO dto, Usuario administrador) {

        if (equipeRepository.findByNome(dto.nome()).isPresent()) {
            throw new IllegalArgumentException("Uma equipe com este nome já existe.");
        }

        Equipe novaEquipe = new Equipe();
        novaEquipe.setNome(dto.nome());
        novaEquipe.setCidade(dto.cidade());
        novaEquipe.setEstado(dto.estado());

        novaEquipe.setAdministrador(administrador);

        return equipeRepository.save(novaEquipe);
    }

    @Transactional(readOnly = true)
    public List<Equipe> buscarEquipesPorAdministrador(Usuario administrador) {
        return equipeRepository.findByAdministrador(administrador);
    }

    @Transactional(readOnly = true)
    public Optional<Equipe> buscarEquipePorIdEAdministrador(Long id, Usuario administrador) {
        return equipeRepository.findByIdAndAdministrador(id, administrador);
    }
}