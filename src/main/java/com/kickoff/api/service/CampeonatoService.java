package com.kickoff.api.service;

import com.kickoff.api.dto.match.*;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.lookup.TipoPartida;
import com.kickoff.api.model.match.*;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.lookup.TipoPartidaRepository;
import com.kickoff.api.repository.match.CampeonatoEquipeRepository;
import com.kickoff.api.repository.match.CampeonatoRepository;
import com.kickoff.api.repository.match.PartidaEventoRepository;
import com.kickoff.api.repository.match.PartidaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
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
    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private TipoPartidaRepository tipoPartidaRepository;

    @Transactional
    public Campeonato criarCampeonato(CampeonatoInputDTO dto) {
        Campeonato c = new Campeonato();
        c.setNome(dto.nome());
        c.setAno(dto.ano());
        c.setDataInicio(dto.dataInicio());
        c.setDataFim(dto.dataFim());
        c.setTipo(TipoCampeonato.valueOf(dto.tipo()));
        c.setStatus(CampeonatoStatus.AGENDADO);
        c.setIdaEVolta(dto.idaEVolta() != null ? dto.idaEVolta() : false);

        if (dto.tipoPartidaId() != null) {
            TipoPartida modalidade = tipoPartidaRepository.findById(dto.tipoPartidaId())
                    .orElseThrow(() -> new EntityNotFoundException("Modalidade inválida"));
            c.setTipoPartida(modalidade);
        }

        return campeonatoRepository.save(c);
    }

    @Transactional
    public void gerarTabela(Long campeonatoId) {
        Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado"));

        if (campeonato.getStatus() != CampeonatoStatus.AGENDADO) {
            throw new IllegalArgumentException("A tabela só pode ser gerada para campeonatos 'AGENDADOS'.");
        }

        List<Equipe> times = campeonatoEquipeRepository.findByCampeonatoId(campeonatoId, Sort.unsorted())
                .stream().map(CampeonatoEquipe::getEquipe).collect(Collectors.toList());

        int minEquipes = (campeonato.getMinEquipes() != null) ? campeonato.getMinEquipes() : 2;
        if (times.size() < minEquipes) {
            throw new IllegalArgumentException("Número insuficiente de equipes. Mínimo exigido: " + minEquipes);
        }

        if (times.size() % 2 != 0) {
            times.add(null);
        }

        int numTimes = times.size();
        int numRodadasTurno = numTimes - 1;
        int jogosPorRodada = numTimes / 2;

        List<Partida> partidasParaSalvar = new ArrayList<>();
        LocalDateTime dataBase = campeonato.getDataInicio().atTime(14, 0);

        List<Partida> partidasTurno = new ArrayList<>();

        for (int rodada = 0; rodada < numRodadasTurno; rodada++) {
            for (int jogo = 0; jogo < jogosPorRodada; jogo++) {
                Equipe timeCasa = times.get(jogo);
                Equipe timeVisitante = times.get(numTimes - 1 - jogo);

                if (timeCasa != null && timeVisitante != null) {
                    Partida p = new Partida();
                    p.setCampeonato(campeonato);

                    if (campeonato.getTipoPartida() != null) {
                        p.setTipoPartida(campeonato.getTipoPartida());
                    }

                    if (rodada % 2 == 0) {
                        p.setEquipeCasa(timeCasa);
                        p.setEquipeVisitante(timeVisitante);
                    } else {
                        p.setEquipeCasa(timeVisitante);
                        p.setEquipeVisitante(timeCasa);
                    }

                    p.setStatus(PartidaStatus.AGENDADA);
                    p.setDataHora(dataBase.plusDays(rodada * 7L)); // 1 semana por rodada
                    p.setLocal("A Definir");
                    partidasTurno.add(p);
                }
            }
            Collections.rotate(times.subList(1, times.size()), 1);
        }

        partidasParaSalvar.addAll(partidasTurno);

        if (Boolean.TRUE.equals(campeonato.getIdaEVolta())) {
            LocalDateTime dataInicioReturno = dataBase.plusDays(numRodadasTurno * 7L);

            for (int i = 0; i < partidasTurno.size(); i++) {
                Partida jogoIda = partidasTurno.get(i);
                long diasDeDiferenca = java.time.Duration.between(dataBase, jogoIda.getDataHora()).toDays();

                Partida jogoVolta = new Partida();
                jogoVolta.setCampeonato(campeonato);
                jogoVolta.setEquipeCasa(jogoIda.getEquipeVisitante());
                jogoVolta.setEquipeVisitante(jogoIda.getEquipeCasa());
                jogoVolta.setStatus(PartidaStatus.AGENDADA);
                jogoVolta.setLocal("A Definir");
                if (campeonato.getTipoPartida() != null) {
                    jogoVolta.setTipoPartida(campeonato.getTipoPartida());
                }

                jogoVolta.setDataHora(dataInicioReturno.plusDays(diasDeDiferenca));

                partidasParaSalvar.add(jogoVolta);
            }
        }

        partidaRepository.saveAll(partidasParaSalvar);

        campeonato.setStatus(CampeonatoStatus.EM_ANDAMENTO);
        campeonatoRepository.save(campeonato);
    }

    @Transactional
    public void finalizarCampeonato(Long campeonatoId) {
        List<TabelaCampeonatoDTO> tabela = buscarTabelaCompleta(campeonatoId);

        if (tabela.isEmpty()) throw new IllegalStateException("Sem times inscritos");

        TabelaCampeonatoDTO campeaoStats = tabela.get(0); // O primeiro da lista ordenada
        Equipe campeao = equipeRepository.findById(campeaoStats.equipeId()).orElseThrow();

        Campeonato campeonato = campeonatoRepository.findById(campeonatoId).orElseThrow();
        campeonato.setEquipeCampeada(campeao);
        campeonato.setStatus(CampeonatoStatus.FINALIZADO);

        campeonatoRepository.save(campeonato);
    }

    public List<CampeonatoResponseDTO> listarTodos() {
        return campeonatoRepository.findAll().stream()
                .map(c -> new CampeonatoResponseDTO(
                        c.getId(),
                        c.getNome(),
                        c.getAno(),
                        c.getDataInicio(),
                        c.getDataFim(),
                        c.getStatus(),
                        c.getMinEquipes(),
                        c.getIdaEVolta(),
                        (c.getTipoPartida() != null) ? c.getTipoPartida().getId() : null
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
                c.getId(), c.getNome(), c.getAno(), c.getDataInicio(), c.getDataFim(), c.getStatus(), c.getMinEquipes(), c.getIdaEVolta(),(c.getTipoPartida() != null) ? c.getTipoPartida().getId() : null
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
    }

    public List<ArtilhariaDTO> buscarArtilharia(Long campeonatoId) {
        return partidaEventoRepository.findArtilhariaDoCampeonato(
                campeonatoId,
                PartidaEventoTipo.GOL
        );
    }
}