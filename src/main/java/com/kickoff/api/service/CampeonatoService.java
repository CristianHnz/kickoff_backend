// src/main/java/com/kickoff/api/service/CampeonatoService.java
package com.kickoff.api.service;

import com.kickoff.api.dto.match.CampeonatoDTO;
import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.repository.match.CampeonatoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CampeonatoService {

    @Autowired
    private CampeonatoRepository campeonatoRepository;

    @Transactional
    public Campeonato criarCampeonato(CampeonatoDTO dto) {
        campeonatoRepository.findByNomeAndAno(dto.nome(), dto.ano()).ifPresent(c -> {
            throw new IllegalArgumentException("Um campeonato com este nome já existe para este ano.");
        });

        if (dto.dataFim().isBefore(dto.dataInicio())) {
            throw new IllegalArgumentException("A data de fim deve ser após a data de início.");
        }

        Campeonato novoCampeonato = new Campeonato();
        novoCampeonato.setNome(dto.nome());
        novoCampeonato.setAno(dto.ano());
        novoCampeonato.setDataInicio(dto.dataInicio());
        novoCampeonato.setDataFim(dto.dataFim());
        return campeonatoRepository.save(novoCampeonato);
    }

    @Transactional(readOnly = true)
    public List<Campeonato> listarCampeonatos() {
        return campeonatoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Campeonato buscarCampeonatoPorId(Long id) {
        return campeonatoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado com o ID: " + id));
    }

    @Transactional
    public Campeonato atualizarCampeonato(Long id, CampeonatoDTO dto) {
        Campeonato campeonatoExistente = buscarCampeonatoPorId(id); // Reutiliza o método que já lança 404

        campeonatoRepository.findByNomeAndAno(dto.nome(), dto.ano()).ifPresent(c -> {
            if (!c.getId().equals(id)) { // Se o ID encontrado for diferente do que estamos editando
                throw new IllegalArgumentException("Um campeonato com este nome já existe para este ano.");
            }
        });

        if (dto.dataFim().isBefore(dto.dataInicio())) {
            throw new IllegalArgumentException("A data de fim deve ser após a data de início.");
        }

        campeonatoExistente.setNome(dto.nome());
        campeonatoExistente.setAno(dto.ano());
        campeonatoExistente.setDataInicio(dto.dataInicio());
        campeonatoExistente.setDataFim(dto.dataFim());

        return campeonatoRepository.save(campeonatoExistente);
    }

    @Transactional
    public void deletarCampeonato(Long id) {
        Campeonato campeonato = buscarCampeonatoPorId(id);
        campeonatoRepository.delete(campeonato);
    }
}