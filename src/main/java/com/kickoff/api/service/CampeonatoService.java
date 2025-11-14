package com.kickoff.api.service;

import com.kickoff.api.dto.match.CampeonatoDetalhesDTO;
import com.kickoff.api.dto.match.CampeonatoInputDTO;
import com.kickoff.api.dto.match.CampeonatoResponseDTO;
import com.kickoff.api.dto.match.TabelaCampeonatoDTO;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.model.match.CampeonatoEquipe;
import com.kickoff.api.model.match.CampeonatoStatus;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.match.CampeonatoEquipeRepository;
import com.kickoff.api.repository.match.CampeonatoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampeonatoService {

    @Autowired
    private CampeonatoRepository campeonatoRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private CampeonatoEquipeRepository campeonatoEquipeRepository;

    @Transactional
    public Campeonato criarCampeonato(CampeonatoInputDTO dto) {
        if (campeonatoRepository.findByNomeAndAno(dto.nome(), dto.ano()).isPresent()) {
            throw new IllegalArgumentException("Já existe um campeonato com este nome no ano " + dto.ano());
        }

        if (dto.dataFim().isBefore(dto.dataInicio())) {
            throw new IllegalArgumentException("A data de término não pode ser anterior à data de início.");
        }

        Campeonato campeonato = new Campeonato();
        campeonato.setNome(dto.nome());
        campeonato.setAno(dto.ano());
        campeonato.setDataInicio(dto.dataInicio());
        campeonato.setDataFim(dto.dataFim());
        campeonato.setStatus(CampeonatoStatus.AGENDADO);

        return campeonatoRepository.save(campeonato);
    }

    public List<CampeonatoResponseDTO> listarTodos() {
        return campeonatoRepository.findAll().stream()
                .map(c -> new CampeonatoResponseDTO(
                        c.getId(),
                        c.getNome(),
                        c.getAno(),
                        c.getDataInicio(),
                        c.getDataFim(),
                        c.getStatus()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void inscreverEquipe(Long campeonatoId, Long equipeId) {
        if (campeonatoEquipeRepository.existsByCampeonatoIdAndEquipeId(campeonatoId, equipeId)) {
            throw new IllegalArgumentException("Esta equipe já está inscrita no campeonato.");
        }

        Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado"));

        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada"));

        CampeonatoEquipe inscricao = new CampeonatoEquipe();
        inscricao.setCampeonato(campeonato);
        inscricao.setEquipe(equipe);

        campeonatoEquipeRepository.save(inscricao);
    }

    public CampeonatoDetalhesDTO buscarDetalhes(Long campeonatoId) {
        Campeonato c = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado"));

        CampeonatoResponseDTO info = new CampeonatoResponseDTO(
                c.getId(), c.getNome(), c.getAno(), c.getDataInicio(), c.getDataFim(), c.getStatus()
        );

        List<TabelaCampeonatoDTO> tabela = listarTabela(campeonatoId);

        return new CampeonatoDetalhesDTO(info, tabela);
    }

    private List<TabelaCampeonatoDTO> listarTabela(Long campeonatoId) {
        Sort sort = Sort.by(
                Sort.Order.desc("pontos"),
                Sort.Order.desc("vitorias"),
                Sort.Order.desc("golsPro")
        );

        return campeonatoEquipeRepository.findByCampeonatoId(campeonatoId, sort).stream()
                .map(this::mapToTabelaDTO)
                .collect(Collectors.toList());
    }

    private TabelaCampeonatoDTO mapToTabelaDTO(CampeonatoEquipe ce) {
        int saldoGols = ce.getGolsPro() - ce.getGolsContra();
        int jogos = ce.getVitorias() + ce.getEmpates() + ce.getDerrotas();

        return new TabelaCampeonatoDTO(
                ce.getEquipe().getId(),
                ce.getEquipe().getNome(),
                ce.getPontos(),
                ce.getVitorias(),
                ce.getEmpates(),
                ce.getDerrotas(),
                ce.getGolsPro(),
                ce.getGolsContra(),
                saldoGols,
                jogos
        );
    }
}