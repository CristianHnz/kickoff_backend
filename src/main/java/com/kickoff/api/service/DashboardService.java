package com.kickoff.api.service;

import com.kickoff.api.dto.core.EquipeDTO;
import com.kickoff.api.dto.dashboard.DashboardGestorDTO;
import com.kickoff.api.dto.match.PartidaResponseDTO;
import com.kickoff.api.dto.match.TabelaCampeonatoDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.match.CampeonatoEquipe;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.match.PartidaStatus;
import com.kickoff.api.repository.auth.UsuarioRepository;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.match.CampeonatoEquipeRepository;
import com.kickoff.api.repository.match.PartidaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EquipeService equipeService;
    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private CampeonatoEquipeRepository campeonatoEquipeRepository;
    @Autowired
    private PartidaService partidaService;
    @Autowired
    private CampeonatoService campeonatoService;

    public DashboardGestorDTO getGestorDashboard(String email) {
        Equipe equipe = equipeService.buscarEquipeDoGestor(email);
        EquipeDTO equipeDTO = new EquipeDTO(equipe.getId(), equipe.getNome(), equipe.getCidade(), equipe.getEstado());
        Long equipeId = equipe.getId();

        PartidaResponseDTO proximoJogo = partidaRepository
                .findTop1ByEquipeCasaIdOrEquipeVisitanteIdAndStatusAndDataHoraAfter(
                        equipeId, equipeId, PartidaStatus.AGENDADA, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "dataHora")
                )
                .map(p -> partidaService.mapToResponseDTO(p))
                .orElse(null);

        PartidaResponseDTO ultimoResultado = partidaRepository
                .findTop1ByEquipeCasaIdOrEquipeVisitanteIdAndStatusAndDataHoraBefore(
                        equipeId, equipeId, PartidaStatus.FINALIZADA, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "dataHora")
                )
                .map(p -> partidaService.mapToResponseDTO(p))
                .orElse(null);

        List<CampeonatoEquipe> minhasInscricoes = campeonatoEquipeRepository.findByEquipeId(equipeId);

        List<TabelaCampeonatoDTO> classificacoes = minhasInscricoes.stream().map(inscricao -> {
                    Long campeonatoId = inscricao.getCampeonato().getId();

                    List<TabelaCampeonatoDTO> tabelaCompleta = campeonatoService.buscarTabelaCompleta(campeonatoId);

                    TabelaCampeonatoDTO minhaPosicaoNaTabela = tabelaCompleta.stream()
                            .filter(t -> t.equipeId().equals(equipeId))
                            .findFirst()
                            .orElse(null);

                    return minhaPosicaoNaTabela;

                }).filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        return new DashboardGestorDTO(equipeDTO, proximoJogo, ultimoResultado, classificacoes);
    }
}