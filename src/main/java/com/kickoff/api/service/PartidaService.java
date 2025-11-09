package com.kickoff.api.service;

import com.kickoff.api.dto.match.PartidaDTO;
import com.kickoff.api.dto.match.PartidaResultadoDTO;
import com.kickoff.api.dto.match.PartidaUpdateDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.match.Campeonato;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.match.PartidaStatus;
import com.kickoff.api.model.role.Arbitro;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.match.CampeonatoRepository;
import com.kickoff.api.repository.match.PartidaRepository;
import com.kickoff.api.repository.role.ArbitroRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class PartidaService {

    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private CampeonatoRepository campeonatoRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private ArbitroRepository arbitroRepository;

    @Transactional
    public Partida criarPartida(PartidaDTO dto) {
        if (dto.equipeCasaId().equals(dto.equipeVisitanteId())) {
            throw new IllegalArgumentException("Equipe mandante e visitante não podem ser a mesma.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (auth != null && auth.getPrincipal() instanceof Usuario u) ? u : null;
        if (usuario == null) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }

        Equipe equipeCasa = equipeRepository.findById(dto.equipeCasaId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe mandante não encontrada."));
        if (equipeCasa.getAdministrador() == null || !equipeCasa.getAdministrador().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Você só pode criar partidas para a sua própria equipe.");
        }

        Equipe equipeVisitante = equipeRepository.findById(dto.equipeVisitanteId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe visitante não encontrada."));

        if (partidaRepository.existsConflitoHorario(dto.dataHora(), equipeCasa.getId()) ||
                partidaRepository.existsConflitoHorario(dto.dataHora(), equipeVisitante.getId())) {
            throw new IllegalArgumentException("Conflito de horário: uma das equipes já possui partida nesse horário.");
        }

        if (partidaRepository.existsMesmosTimesMesmoHorario(dto.dataHora(), equipeCasa.getId(), equipeVisitante.getId())) {
            throw new IllegalArgumentException("Já existe uma partida entre essas equipes nesse horário.");
        }

        Campeonato camp = null;
        if (dto.campeonatoId() != null) {
            camp = campeonatoRepository.findById(dto.campeonatoId())
                    .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado."));
        }

        Arbitro arbitro = null;
        if (dto.arbitroId() != null) {
            arbitro = arbitroRepository.findById(dto.arbitroId())
                    .orElseThrow(() -> new EntityNotFoundException("Árbitro não encontrado."));
        }

        Partida p = new Partida();
        p.setCampeonato(camp);
        p.setEquipeCasa(equipeCasa);
        p.setEquipeVisitante(equipeVisitante);
        p.setArbitro(arbitro);
        p.setDataHora(dto.dataHora());
        p.setLocal(dto.local());
        p.setPlacarCasa(0);
        p.setPlacarVisitante(0);
        p.setStatus(PartidaStatus.AGENDADA);

        return partidaRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<Partida> listarPartidas(Long campeonatoId) {
        if (campeonatoId != null) {
            Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                    .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado."));
            return partidaRepository.findByCampeonato(campeonato);
        } else {
            return partidaRepository.findAll();
        }
    }

    @Transactional(readOnly = true)
    public Partida buscarPartidaPorId(Long id) {
        return partidaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partida não encontrada com o ID: " + id));
    }

    @Transactional
    public Partida atualizarPartida(Long id, PartidaDTO dto) {
        Partida partidaExistente = buscarPartidaPorId(id);

        Campeonato campeonato = campeonatoRepository.findById(dto.campeonatoId())
                .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado."));

        Equipe equipeCasa = equipeRepository.findById(dto.equipeCasaId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe da casa não encontrada."));

        Equipe equipeVisitante = equipeRepository.findById(dto.equipeVisitanteId())
                .orElseThrow(() -> new EntityNotFoundException("Equipe visitante não encontrada."));

        Arbitro arbitro = arbitroRepository.findById(dto.arbitroId())
                .orElseThrow(() -> new EntityNotFoundException("Árbitro não encontrado."));

        if (equipeCasa.getId().equals(equipeVisitante.getId())) {
            throw new IllegalArgumentException("Equipe da casa e visitante não podem ser a mesma.");
        }

        LocalDateTime inicioCampeonato = campeonato.getDataInicio().atStartOfDay();
        LocalDateTime fimCampeonato = campeonato.getDataFim().atTime(23, 59, 59);

        if (dto.dataHora().isBefore(inicioCampeonato) || dto.dataHora().isAfter(fimCampeonato)) {
            throw new IllegalArgumentException("A data da partida está fora do período do campeonato.");
        }

        partidaExistente.setCampeonato(campeonato);
        partidaExistente.setEquipeCasa(equipeCasa);
        partidaExistente.setEquipeVisitante(equipeVisitante);
        partidaExistente.setArbitro(arbitro);
        partidaExistente.setDataHora(dto.dataHora());
        partidaExistente.setLocal(dto.local());

        return partidaRepository.save(partidaExistente);
    }

    @Transactional
    public void deletarPartida(Long id) {
        Partida partida = buscarPartidaPorId(id);
        partidaRepository.delete(partida);
    }

    private void validarTransicaoDeStatus(PartidaStatus atual, PartidaStatus novo) {
        switch (atual) {
            case AGENDADA -> {
                if (novo != PartidaStatus.CONFIRMADA && novo != PartidaStatus.CANCELADA) {
                    throw new IllegalArgumentException("Uma partida AGENDADA só pode ser CONFIRMADA ou CANCELADA.");
                }
            }
            case CONFIRMADA -> {
                if (novo != PartidaStatus.EM_ANDAMENTO && novo != PartidaStatus.CANCELADA) {
                    throw new IllegalArgumentException("Uma partida CONFIRMADA só pode iniciar ou ser cancelada.");
                }
            }
            case EM_ANDAMENTO -> {
                if (novo != PartidaStatus.FINALIZADA) {
                    throw new IllegalArgumentException("Uma partida EM ANDAMENTO só pode ser FINALIZADA.");
                }
            }
            case FINALIZADA, CANCELADA -> {
                throw new IllegalArgumentException("Partidas FINALIZADAS ou CANCELADAS não podem ser alteradas.");
            }
        }
    }

    @Transactional
    public Partida atualizarResultado(Long id, PartidaResultadoDTO dto) {
        Partida partidaExistente = buscarPartidaPorId(id);
        partidaExistente.setPlacarCasa(dto.placarCasa());
        partidaExistente.setPlacarVisitante(dto.placarVisitante());
        partidaExistente.setStatus(dto.status());
        return partidaRepository.save(partidaExistente);
    }

    @Transactional
    public Partida atualizarStatus(Long id, PartidaStatus novoStatus) {
        Partida partida = buscarPartidaPorId(id);

        validarTransicaoDeStatus(partida.getStatus(), novoStatus);

        partida.setStatus(novoStatus);
        return partidaRepository.save(partida);
    }

    @Transactional
    public Partida editarParcial(Long id, PartidaUpdateDTO dto) {
        Partida p = buscarPartidaPorId(id);

        // campeonato (pode ser removido)
        if (dto.campeonatoId != null) {
            if (dto.campeonatoId == 0L) throw new IllegalArgumentException("campeonatoId inválido");
            Campeonato c = campeonatoRepository.findById(dto.campeonatoId)
                    .orElseThrow(() -> new EntityNotFoundException("Campeonato não encontrado."));
            p.setCampeonato(c);
        } else if (dto.campeonatoId == null && fieldExplicitlyPresent(dto, "campeonatoId")) {
            // campo presente com null -> limpar
            p.setCampeonato(null);
        }

        // equipes (quando presentes)
        if (dto.equipeCasaId != null) {
            Equipe casa = equipeRepository.findById(dto.equipeCasaId)
                    .orElseThrow(() -> new EntityNotFoundException("Equipe da casa não encontrada."));
            p.setEquipeCasa(casa);
        }
        if (dto.equipeVisitanteId != null) {
            Equipe fora = equipeRepository.findById(dto.equipeVisitanteId)
                    .orElseThrow(() -> new EntityNotFoundException("Equipe visitante não encontrada."));
            p.setEquipeVisitante(fora);
        }
        if (p.getEquipeCasa() != null && p.getEquipeVisitante() != null &&
                p.getEquipeCasa().getId().equals(p.getEquipeVisitante().getId())) {
            throw new IllegalArgumentException("Equipe da casa e visitante não podem ser a mesma.");
        }

        if (dto.arbitroId != null) {
            Arbitro a = arbitroRepository.findById(dto.arbitroId)
                    .orElseThrow(() -> new EntityNotFoundException("Árbitro não encontrado."));
            p.setArbitro(a);
        } else if (fieldExplicitlyPresent(dto, "arbitroId")) {
            p.setArbitro(null);
        }

        if (dto.dataHora != null) {
            p.setDataHora(dto.dataHora);
        }

        if (dto.local != null) {
            p.setLocal(dto.local.trim());
        } else if (fieldExplicitlyPresent(dto, "local")) {
            p.setLocal(null);
        }

        return partidaRepository.save(p);
    }

    private boolean fieldExplicitlyPresent(Object dto, String field) {
        try {
            var f = dto.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.get(dto) == null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }

}