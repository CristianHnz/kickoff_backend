package com.kickoff.api.service;

import com.kickoff.api.dto.match.*;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.model.match.CampeonatoEquipe;
import com.kickoff.api.model.match.CampeonatoStatus;
import com.kickoff.api.model.match.PartidaEventoTipo;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.match.CampeonatoEquipeRepository;
import com.kickoff.api.repository.match.CampeonatoRepository;
import com.kickoff.api.repository.match.PartidaEventoRepository;
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
    @Autowired
    private PartidaEventoRepository partidaEventoRepository;

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

        List<TabelaCampeonatoDTO> tabela = buscarTabelaCompleta(campeonatoId);

        return new CampeonatoDetalhesDTO(info, tabela);
    }

    public List<TabelaCampeonatoDTO> buscarTabelaCompleta(Long campeonatoId) {
        Sort sort = Sort.by(
                Sort.Order.desc("pontos"),
                Sort.Order.desc("vitorias"),
                Sort.Order.desc("golsPro")
        );

        List<CampeonatoEquipe> inscricoesOrdenadas =
                campeonatoEquipeRepository.findByCampeonatoId(campeonatoId, sort);

        List<TabelaCampeonatoDTO> tabela = new java.util.ArrayList<>();

        for (int i = 0; i < inscricoesOrdenadas.size(); i++) {
            CampeonatoEquipe ce = inscricoesOrdenadas.get(i);
            int posicao = i + 1;
            tabela.add(mapToTabelaDTO(ce, posicao));
        }

        return tabela;
    }

    public TabelaCampeonatoDTO mapToTabelaDTO(CampeonatoEquipe ce, int posicao) {
        int saldoGols = ce.getGolsPro() - ce.getGolsContra();
        int jogos = ce.getVitorias() + ce.getEmpates() + ce.getDerrotas();

        return new TabelaCampeonatoDTO(
                ce.getCampeonato().getId(),
                ce.getCampeonato().getNome(),
                posicao,
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

    @Transactional
    public Campeonato atualizarCampeonato(Long id, CampeonatoInputDTO dto) {
        Campeonato campeonato = campeonatoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado"));

        if (campeonato.getStatus() != CampeonatoStatus.AGENDADO) {
            throw new IllegalArgumentException("Só é permitido alterar campeonatos com status 'AGENDADO'.");
        }

        if (!campeonato.getNome().equals(dto.nome()) || !campeonato.getAno().equals(dto.ano())) {
            if (campeonatoRepository.findByNomeAndAno(dto.nome(), dto.ano()).isPresent()) {
                throw new IllegalArgumentException("Já existe um campeonato com este nome no ano " + dto.ano());
            }
        }

        if (dto.dataFim().isBefore(dto.dataInicio())) {
            throw new IllegalArgumentException("A data de término não pode ser anterior à data de início.");
        }

        campeonato.setNome(dto.nome());
        campeonato.setAno(dto.ano());
        campeonato.setDataInicio(dto.dataInicio());
        campeonato.setDataFim(dto.dataFim());

        return campeonatoRepository.save(campeonato);
    }

    @Transactional
    public void cancelarCampeonato(Long id) {
        Campeonato campeonato = campeonatoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado"));

        if (campeonato.getStatus() == CampeonatoStatus.FINALIZADO) {
            throw new IllegalArgumentException("Não é possível cancelar um campeonato já finalizado.");
        }

        campeonato.setStatus(CampeonatoStatus.CANCELADO);
        campeonatoRepository.save(campeonato);

        // List<Partida> partidas = partidaRepository.findByCampeonatoId(id);
        // partidas.forEach(p -> p.setStatus(PartidaStatus.CANCELADA));
        // partidaRepository.saveAll(partidas);
    }

    public List<ArtilhariaDTO> buscarArtilharia(Long campeonatoId) {
        return partidaEventoRepository.findArtilhariaDoCampeonato(
                campeonatoId,
                PartidaEventoTipo.GOL
        );
    }
}