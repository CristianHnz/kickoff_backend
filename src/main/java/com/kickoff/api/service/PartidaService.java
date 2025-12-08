package com.kickoff.api.service;

import com.kickoff.api.dto.match.*;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.lookup.TipoPartida;
import com.kickoff.api.model.match.*;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.lookup.TipoPartidaRepository;
import com.kickoff.api.repository.match.CampeonatoEquipeRepository;
import com.kickoff.api.repository.match.CampeonatoRepository;
import com.kickoff.api.repository.match.PartidaJogadorRepository;
import com.kickoff.api.repository.match.PartidaRepository;
import com.kickoff.api.repository.relationship.JogadorEquipeRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartidaService {

    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private CampeonatoEquipeRepository campeonatoEquipeRepository;
    @Autowired
    private CampeonatoRepository campeonatoRepository;
    @Autowired
    private TipoPartidaRepository tipoPartidaRepository;
    @Autowired
    private JogadorEquipeRepository jogadorEquipeRepository;
    @Autowired private PartidaJogadorRepository partidaJogadorRepository;
    @Autowired private JogadorRepository jogadorRepository;

    @Transactional
    public Partida agendarPartida(PartidaInputDTO dto) {

        TipoPartida tipoPartida = tipoPartidaRepository.findById(dto.tipoPartidaId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de partida inválido."));
        int minJogadores = tipoPartida.getMinJogadores();

        if (dto.equipeCasaId().equals(dto.equipeVisitanteId())) {
            throw new IllegalArgumentException("Uma equipe não pode jogar contra si mesma.");
        }

        Equipe casa = equipeRepository.findById(dto.equipeCasaId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe da casa não encontrada"));

        Equipe visitante = equipeRepository.findById(dto.equipeVisitanteId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe visitante não encontrada"));

        int jogadoresCasa = jogadorEquipeRepository.findAtivosByEquipeId(casa.getId()).size();
        int jogadoresVisitante = jogadorEquipeRepository.findAtivosByEquipeId(visitante.getId()).size();

        if (jogadoresCasa < minJogadores) {
            throw new IllegalArgumentException(
                    "Time da Casa (" + casa.getNome() + ") não possui o mínimo de " +
                            minJogadores + " jogadores. (Atual: " + jogadoresCasa + ")"
            );
        }
        if (jogadoresVisitante < minJogadores) {
            throw new IllegalArgumentException(
                    "Time Visitante (" + visitante.getNome() + ") não possui o mínimo de " +
                            minJogadores + " jogadores. (Atual: " + jogadoresVisitante + ")"
            );
        }

        Partida partida = new Partida();
        partida.setTipoPartida(tipoPartida);
        partida.setEquipeCasa(casa);
        partida.setEquipeVisitante(visitante);
        partida.setDataHora(dto.dataHora());
        partida.setLocal(dto.local());
        partida.setStatus(PartidaStatus.AGENDADA);
        partida.setPlacarCasa(0);
        partida.setPlacarVisitante(0);

        if (dto.campeonatoId() != null) {
            Campeonato c = campeonatoRepository.findById(dto.campeonatoId())
                    .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado"));
            partida.setCampeonato(c);
        }

        if (dto.fase() != null && !dto.fase().isBlank()) {
            partida.setFase(FaseMataMata.valueOf(dto.fase()));
        }

        return partidaRepository.save(partida);
    }

    public List<PartidaResponseDTO> listarPartidasDoTime(Long equipeId) {
        return partidaRepository.findPartidasPorEquipe(equipeId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void atualizarStatus(Long id, String statusStr) {
        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));
        try {
            PartidaStatus status = PartidaStatus.valueOf(statusStr);
            if (status == PartidaStatus.EM_ANDAMENTO && partida.getDataHoraInicioReal() == null) {
                partida.setDataHoraInicioReal(
                        LocalDateTime.now());
            }
            partida.setStatus(status);
            partidaRepository.save(partida);

            if (status == PartidaStatus.FINALIZADA && partida.getCampeonato() != null) {
                processarResultadoCampeonato(partida);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + statusStr);
        }
    }

    private void processarResultadoCampeonato(Partida partida) {
        Long campeonatoId = partida.getCampeonato().getId();

        CampeonatoEquipe timeCasaStats = campeonatoEquipeRepository
                .findByCampeonatoIdAndEquipeId(campeonatoId, partida.getEquipeCasa().getId())
                .orElse(null);

        CampeonatoEquipe timeVisitanteStats = campeonatoEquipeRepository
                .findByCampeonatoIdAndEquipeId(campeonatoId, partida.getEquipeVisitante().getId())
                .orElse(null);

        if (timeCasaStats != null && timeVisitanteStats != null) {

            int placarCasa = partida.getPlacarCasa();
            int placarVisitante = partida.getPlacarVisitante();

            timeCasaStats.setGolsPro(timeCasaStats.getGolsPro() + placarCasa);
            timeCasaStats.setGolsContra(timeCasaStats.getGolsContra() + placarVisitante);

            timeVisitanteStats.setGolsPro(timeVisitanteStats.getGolsPro() + placarVisitante);
            timeVisitanteStats.setGolsContra(timeVisitanteStats.getGolsContra() + placarCasa);

            if (placarCasa > placarVisitante) {
                timeCasaStats.setPontos(timeCasaStats.getPontos() + 3);
                timeCasaStats.setVitorias(timeCasaStats.getVitorias() + 1);
                timeVisitanteStats.setDerrotas(timeVisitanteStats.getDerrotas() + 1);
            } else if (placarVisitante > placarCasa) {
                timeVisitanteStats.setPontos(timeVisitanteStats.getPontos() + 3);
                timeVisitanteStats.setVitorias(timeVisitanteStats.getVitorias() + 1);
                timeCasaStats.setDerrotas(timeCasaStats.getDerrotas() + 1);
            } else {
                timeCasaStats.setPontos(timeCasaStats.getPontos() + 1);
                timeCasaStats.setEmpates(timeCasaStats.getEmpates() + 1);
                timeVisitanteStats.setPontos(timeVisitanteStats.getPontos() + 1);
                timeVisitanteStats.setEmpates(timeVisitanteStats.getEmpates() + 1);
            }

            campeonatoEquipeRepository.save(timeCasaStats);
            campeonatoEquipeRepository.save(timeVisitanteStats);
        }
    }

    public PartidaResponseDTO buscarPorId(Long id) {
        Partida p = partidaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));

        return mapToResponseDTO(p);
    }

    public PartidaResponseDTO mapToResponseDTO(Partida p) {
        int minJogadores = (p.getTipoPartida() != null) ? p.getTipoPartida().getMinJogadores() : 0;
        int duracao = (p.getTipoPartida() != null) ? p.getTipoPartida().getDuracaoMinutos() : 90;
        Long campeonatoId = (p.getCampeonato() != null) ? p.getCampeonato().getId() : null;
        Long tempoAcumulado = (p.getTempoJogadoSegundos() != null) ? p.getTempoJogadoSegundos() : 0L;
        String periodoAtual = (p.getPeriodo() != null) ? p.getPeriodo().name() : "NAO_INICIADO";
        String nomeCampeonato = (p.getCampeonato() != null) ? p.getCampeonato().getNome() : "Amistoso";
        Long tipoPartidaId = (p.getTipoPartida() != null) ? p.getTipoPartida().getId() : null;
        String fase = (p.getFase() != null) ? p.getFase().name() : null;

        return new PartidaResponseDTO(
                p.getId(),
                p.getDataHora(),
                p.getLocal(),
                p.getEquipeCasa().getNome(),
                p.getEquipeVisitante().getNome(),
                p.getPlacarCasa(),
                p.getPlacarVisitante(),
                p.getStatus(),
                p.getEquipeCasa().getId(),
                p.getEquipeVisitante().getId(),
                campeonatoId,
                nomeCampeonato,
                minJogadores,
                duracao,
                p.getDataHoraInicioReal(),
                periodoAtual,
                tempoAcumulado,
                tipoPartidaId,
                fase
        );
    }

    @Transactional
    public void controlarPartida(Long id, String acao, boolean forcarFim) {
        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));

        PeriodoPartida atual = partida.getPeriodo();
        LocalDateTime agora = LocalDateTime.now();

        if (partida.getTempoJogadoSegundos() == null) {
            partida.setTempoJogadoSegundos(0L);
        }
        switch (acao) {
            case "INICIAR_JOGO":
                if (atual != PeriodoPartida.NAO_INICIADO) throw new IllegalStateException("Jogo já iniciado");
                partida.setPeriodo(PeriodoPartida.PRIMEIRO_TEMPO);
                partida.setStatus(PartidaStatus.EM_ANDAMENTO);
                partida.setDataHoraInicioReal(agora);
                break;

            case "FIM_PRIMEIRO_TEMPO":
                if (atual != PeriodoPartida.PRIMEIRO_TEMPO) throw new IllegalStateException("Não está no 1º tempo");

                long segundos1T = java.time.Duration.between(partida.getDataHoraInicioReal(), agora).getSeconds();

                int duracaoTotal = partida.getTipoPartida().getDuracaoMinutos();
                long duracaoMeioTempo = (duracaoTotal / 2) * 60L;

                if (segundos1T < duracaoMeioTempo && !forcarFim) {
                    long faltam = (duracaoMeioTempo - segundos1T) / 60;
                    throw new IllegalArgumentException("O 1º tempo ainda não atingiu o tempo regulamentar (" + (duracaoTotal/2) + " min). Faltam aprox. " + faltam + " minutos. Deseja encerrar o 1º tempo mesmo assim?");
                }
                partida.setTempoJogadoSegundos(partida.getTempoJogadoSegundos() + segundos1T);

                partida.setPeriodo(PeriodoPartida.INTERVALO);
                partida.setDataHoraInicioReal(null);
                break;

            case "INICIAR_SEGUNDO_TEMPO":
                if (atual != PeriodoPartida.INTERVALO) throw new IllegalStateException("Jogo não está no intervalo");
                partida.setPeriodo(PeriodoPartida.SEGUNDO_TEMPO);
                partida.setDataHoraInicioReal(agora);
                break;

            case "ENCERRAR_JOGO":
                if (atual != PeriodoPartida.SEGUNDO_TEMPO) throw new IllegalStateException("Jogo deve estar no 2º tempo para encerrar");

                long segundos2T = java.time.Duration.between(partida.getDataHoraInicioReal(), agora).getSeconds();
                long totalSegundos = partida.getTempoJogadoSegundos() + segundos2T;

                int duracaoMinutos = partida.getTipoPartida().getDuracaoMinutos();
                long duracaoSegundos = duracaoMinutos * 60L;

                if (totalSegundos < duracaoSegundos && !forcarFim) {
                    long faltam = (duracaoSegundos - totalSegundos) / 60;
                    throw new IllegalArgumentException("O jogo ainda não atingiu o tempo regulamentar (" + duracaoMinutos + " min). Faltam aprox. " + faltam + " minutos. Use a opção de forçar encerramento se necessário.");
                }
                partida.setTempoJogadoSegundos(totalSegundos);
                partida.setPeriodo(PeriodoPartida.FIM_DE_JOGO);
                partida.setStatus(PartidaStatus.FINALIZADA);
                partida.setDataHoraInicioReal(null);
                if (partida.getCampeonato() != null) {
                    processarResultadoCampeonato(partida);
                }
                break;

            default:
                throw new IllegalArgumentException("Ação desconhecida: " + acao);
        }

        partidaRepository.save(partida);
    }

    @Transactional
    public Partida atualizarPartida(Long partidaId, PartidaInputDTO dto) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));

        if (partida.getStatus() != PartidaStatus.AGENDADA) {
            throw new IllegalArgumentException("Só é permitido alterar partidas com status 'AGENDADA'.");
        }

        if (dto.equipeCasaId().equals(dto.equipeVisitanteId())) {
            throw new IllegalArgumentException("Uma equipe não pode jogar contra si mesma.");
        }

        Equipe casa = equipeRepository.findById(dto.equipeCasaId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe da casa não encontrada"));

        Equipe visitante = equipeRepository.findById(dto.equipeVisitanteId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe visitante não encontrada"));

        partida.setEquipeCasa(casa);
        partida.setEquipeVisitante(visitante);
        partida.setDataHora(dto.dataHora());
        partida.setLocal(dto.local());

        if (dto.tipoPartidaId() != null) {
            TipoPartida tipo = tipoPartidaRepository.findById(dto.tipoPartidaId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de partida não encontrado"));
            partida.setTipoPartida(tipo);
        }

        if (dto.campeonatoId() != null) {
            Campeonato c = campeonatoRepository.findById(dto.campeonatoId())
                    .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado"));
            partida.setCampeonato(c);
        } else {
            partida.setCampeonato(null);
        }

        if (dto.fase() != null && !dto.fase().isBlank()) {
            partida.setFase(FaseMataMata.valueOf(dto.fase()));
        } else {
            partida.setFase(null);
        }

        return partidaRepository.save(partida);
    }

    @Transactional
    public void cancelarPartida(Long partidaId) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));

        if (partida.getStatus() != PartidaStatus.AGENDADA) {
            throw new IllegalArgumentException("Não é possível cancelar uma partida que já está 'EM ANDAMENTO' ou 'FINALIZADA'.");
        }

        partida.setStatus(PartidaStatus.CANCELADA);
        partidaRepository.save(partida);
    }

    @Transactional
    public void salvarEscalacao(Long partidaId, Long equipeId, SalvarEscalacaoDTO dto) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada"));
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada"));

        partidaJogadorRepository.deleteByPartidaIdAndEquipeId(partidaId, equipeId);

        for (JogadorEscaladoInputDTO jDto : dto.escalacao()) {
            Jogador jogador = jogadorRepository.findById(jDto.jogadorId())
                    .orElseThrow(() -> new EntityNotFoundException("Jogador ID: " + jDto.jogadorId() + " não encontrado"));

            PartidaJogador pj = new PartidaJogador();
            pj.setPartida(partida);
            pj.setEquipe(equipe);
            pj.setJogador(jogador);
            pj.setNumeroCamisa(jDto.numeroCamisa());
            pj.setStatusJogador(jDto.status());

            partidaJogadorRepository.save(pj);
        }
    }

    public List<JogadorEscaladoResponseDTO> listarEscalados(Long partidaId) {
        return partidaJogadorRepository.findByPartidaId(partidaId).stream()
                .map(pj -> new JogadorEscaladoResponseDTO(
                        pj.getJogador().getId(),
                        pj.getJogador().getPessoa().getNome(),
                        pj.getNumeroCamisa(),
                        pj.getStatusJogador().name(),
                        pj.getEquipe().getId()
                ))
                .collect(Collectors.toList());
    }

    public List<PartidaResponseDTO> listarPartidasEmAndamento() {
        return partidaRepository.findByStatus(PartidaStatus.EM_ANDAMENTO).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PartidaResponseDTO> listarPartidasPorCampeonato(Long campeonatoId) {
        return partidaRepository.findByCampeonatoIdOrderByDataHoraAsc(campeonatoId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
}