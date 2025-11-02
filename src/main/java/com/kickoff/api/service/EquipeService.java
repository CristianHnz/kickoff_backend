package com.kickoff.api.service;

import com.kickoff.api.dto.core.EquipeDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.repository.core.EquipeRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Transactional
    public Equipe atualizarEquipe(Long id, EquipeDTO dto, Usuario administrador) {
        Equipe equipeExistente = equipeRepository.findByIdAndAdministrador(id, administrador)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada ou você não tem permissão para editá-la."));

        if (equipeRepository.findByNomeAndIdNot(dto.nome(), id).isPresent()) {
            throw new IllegalArgumentException("O nome '" + dto.nome() + "' já está em uso por outra equipe.");
        }
        equipeExistente.setNome(dto.nome());
        equipeExistente.setCidade(dto.cidade());
        equipeExistente.setEstado(dto.estado());

        return equipeRepository.save(equipeExistente);
    }

    @Transactional
    public void deletarEquipe(Long id, Usuario administrador) {
        Equipe equipe = equipeRepository.findByIdAndAdministrador(id, administrador)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada ou você não tem permissão para excluí-la."));
        equipeRepository.delete(equipe);
    }
}