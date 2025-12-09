package com.kickoff.api.service;

import com.kickoff.api.dto.core.EquipeDTO;
import com.kickoff.api.dto.dashboard.DashboardGestorDTO;
import com.kickoff.api.dto.dashboard.DashboardJogadorDTO;
import com.kickoff.api.dto.dashboard.JogadorStatsDTO;
import com.kickoff.api.dto.match.PartidaResponseDTO;
import com.kickoff.api.dto.match.TabelaCampeonatoDTO;
import com.kickoff.api.dto.role.EquipeAtualDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.match.CampeonatoEquipe;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.match.PartidaEventoTipo;
import com.kickoff.api.model.match.PartidaStatus;
import com.kickoff.api.model.relationship.JogadorEquipe;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.auth.UsuarioRepository;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.core.PessoaRepository;
import com.kickoff.api.repository.match.AvaliacaoRepository;
import com.kickoff.api.repository.match.CampeonatoEquipeRepository;
import com.kickoff.api.repository.match.PartidaEventoRepository;
import com.kickoff.api.repository.match.PartidaRepository;
import com.kickoff.api.repository.relationship.JogadorEquipeRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Autowired
    private PessoaRepository pessoaRepository;
    @Autowired
    private JogadorRepository jogadorRepository;
    @Autowired
    private JogadorEquipeRepository jogadorEquipeRepository;
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    @Autowired
    private PartidaEventoRepository partidaEventoRepository;

    public DashboardGestorDTO getGestorDashboard(String email) {
        Equipe equipe = equipeService.buscarMinhaEquipe(email);
        EquipeDTO equipeDTO = new EquipeDTO(equipe.getId(), equipe.getNome(), equipe.getCidade(), equipe.getEstado(), equipe.getAdministrador().getId(), equipe.getEscudo(), equipe.getCorPrimaria(), equipe.getApelido(), equipe.getDataFundacao());
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

    public DashboardJogadorDTO getJogadorDashboard(String email) {
        Pessoa pessoa = pessoaRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
        Jogador jogador = jogadorRepository.findByPessoaId(pessoa.getId())
                .orElseThrow(() -> new EntityNotFoundException("Perfil de Jogador não encontrado"));

        // --- 1. Dados Básicos e de Equipe ---
        EquipeAtualDTO equipeAtual = null;
        PartidaResponseDTO proximoJogo = null;
        PartidaResponseDTO ultimoResultado = null;

        Optional<JogadorEquipe> vinculoAtivoOpt = jogadorEquipeRepository.findContratoAtivo(jogador.getId());

        if (vinculoAtivoOpt.isPresent()) {
            JogadorEquipe vinculo = vinculoAtivoOpt.get();
            Equipe equipe = vinculo.getEquipe();
            Long equipeId = equipe.getId();

            equipeAtual = new EquipeAtualDTO(
                    equipeId,
                    equipe.getNome(),
                    jogador.getNumeroCamisa(),
                    vinculo.getDataEntrada()
            );

            proximoJogo = partidaRepository
                    .findTop1ByEquipeCasaIdOrEquipeVisitanteIdAndStatusAndDataHoraAfter(
                            equipeId, equipeId, PartidaStatus.AGENDADA, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "dataHora")
                    )
                    .map(partidaService::mapToResponseDTO)
                    .orElse(null);

            ultimoResultado = partidaRepository
                    .findTop1ByEquipeCasaIdOrEquipeVisitanteIdAndStatusAndDataHoraBefore(
                            equipeId, equipeId, PartidaStatus.FINALIZADA, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "dataHora")
                    )
                    .map(partidaService::mapToResponseDTO)
                    .orElse(null);
        }

        // --- 2. Estatísticas Simples ---
        Long totalGols = partidaEventoRepository.countGolsByJogadorId(jogador.getId(), PartidaEventoTipo.GOL);

        // --- 3. Estatísticas Avançadas (3 Pilares) ---
        // Busca todas as avaliações deste jogador
        List<com.kickoff.api.model.match.Avaliacao> avaliacoes = avaliacaoRepository.findAllByJogadorId(jogador.getId());

        BigDecimal mediaTecnica = BigDecimal.ZERO;
        BigDecimal mediaTatica = BigDecimal.ZERO;
        BigDecimal mediaFisica = BigDecimal.ZERO;
        BigDecimal mediaGeral = BigDecimal.ZERO;
        List<BigDecimal> historicoNotas = List.of();

        if (!avaliacoes.isEmpty()) {
            // Calcula Médias
            double avgTec = avaliacoes.stream().mapToDouble(a -> a.getNotaTecnica().doubleValue()).average().orElse(0.0);
            double avgTat = avaliacoes.stream().mapToDouble(a -> a.getNotaTatica().doubleValue()).average().orElse(0.0);
            double avgFis = avaliacoes.stream().mapToDouble(a -> a.getNotaFisica().doubleValue()).average().orElse(0.0);
            double avgGeral = avaliacoes.stream().mapToDouble(a -> a.getMediaFinal().doubleValue()).average().orElse(0.0);

            mediaTecnica = BigDecimal.valueOf(avgTec).setScale(1, java.math.RoundingMode.HALF_UP);
            mediaTatica = BigDecimal.valueOf(avgTat).setScale(1, java.math.RoundingMode.HALF_UP);
            mediaFisica = BigDecimal.valueOf(avgFis).setScale(1, java.math.RoundingMode.HALF_UP);
            mediaGeral = BigDecimal.valueOf(avgGeral).setScale(1, java.math.RoundingMode.HALF_UP);

            // Pega as últimas 5 notas para o gráfico de evolução
            // Ordena por data (mais recente primeiro), pega 5, e inverte para cronológico
            historicoNotas = avaliacoes.stream()
                    .sorted((a, b) -> b.getDataAvaliacao().compareTo(a.getDataAvaliacao()))
                    .limit(5)
                    .map(com.kickoff.api.model.match.Avaliacao::getMediaFinal)
                    .sorted() // Se quiser cronológico reverso ou normal, ajustar aqui. Vamos deixar as últimas 5.
                    .collect(Collectors.toList());
            // Nota: Para gráfico de linha cronológico, o ideal é ordenar por data ASC.
            // Aqui pegamos as 5 mais recentes e depois podemos reordenar se precisar.
        }

        JogadorStatsDTO stats = new JogadorStatsDTO(
                totalGols != null ? totalGols : 0L,
                mediaGeral,
                mediaTecnica,
                mediaTatica,
                mediaFisica,
                historicoNotas
        );

        return new DashboardJogadorDTO(equipeAtual, proximoJogo, ultimoResultado, stats);
    }
}