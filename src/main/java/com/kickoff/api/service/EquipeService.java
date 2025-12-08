package com.kickoff.api.service;

import com.kickoff.api.dto.core.EquipeDTO;
import com.kickoff.api.dto.team.EquipeStatsDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.match.Partida;
import com.kickoff.api.model.match.PartidaStatus;
import com.kickoff.api.model.relationship.ComissaoTecnicaEquipe;
import com.kickoff.api.model.relationship.JogadorEquipe;
import com.kickoff.api.model.role.ComissaoTecnica;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.auth.UsuarioRepository;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.match.PartidaRepository;
import com.kickoff.api.repository.relationship.ComissaoTecnicaEquipeRepository;
import com.kickoff.api.repository.relationship.JogadorEquipeRepository;
import com.kickoff.api.repository.role.ComissaoTecnicaRepository;
import com.kickoff.api.repository.role.JogadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private JogadorRepository jogadorRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private JogadorEquipeRepository jogadorEquipeRepository;
    @Autowired
    private ComissaoTecnicaRepository comissaoTecnicaRepository;
    @Autowired
    private ComissaoTecnicaEquipeRepository comissaoEquipeRepository;
    @Autowired
    private PartidaRepository partidaRepository;

    @Transactional
    public Equipe criarEquipe(EquipeDTO dto, String email) {

        Usuario administrador = usuarioRepository.findByPessoaEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário administrador não encontrado"));

        if (equipeRepository.existsByNome(dto.nome())) {
            throw new IllegalArgumentException("Já existe uma equipe com o nome '" + dto.nome() + "'.");
        }

        if (equipeRepository.findByAdministradorId(administrador.getId()).isPresent()) {
            throw new IllegalArgumentException("Você já possui uma equipe cadastrada.");
        }

        Equipe equipe = new Equipe();
        equipe.setNome(dto.nome());
        equipe.setCidade(dto.cidade());
        equipe.setEstado(dto.estado().toUpperCase());
        equipe.setAdministrador(administrador);
        equipe.setEscudo(dto.escudo());
        equipe.setCorPrimaria(dto.corPrimaria());
        equipe.setApelido(dto.apelido());
        equipe.setDataFundacao(dto.dataFundacao());

        return equipeRepository.save(equipe);
    }

    public List<EquipeDTO> listarTodas() {
        return equipeRepository.findAll().stream()
                .map(equipe -> new EquipeDTO(
                        equipe.getId(),
                        equipe.getNome(),
                        equipe.getCidade(),
                        equipe.getEstado(),
                        equipe.getAdministrador().getId(),
                        equipe.getEscudo(),
                        equipe.getCorPrimaria(),
                        equipe.getApelido(),
                        equipe.getDataFundacao()
                ))
                .collect(Collectors.toList());
    }

    public Equipe buscarMinhaEquipe(String email) {
        Usuario usuario = usuarioRepository.findByPessoaEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        String role = usuario.getRole();

        if ("ROLE_GESTOR_EQUIPE".equals(role)) {
            return equipeRepository.findByAdministradorId(usuario.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Nenhuma equipe encontrada para este gestor."));

        } else if ("ROLE_JOGADOR".equals(role)) {
            Jogador jogador = jogadorRepository.findByPessoaId(usuario.getPessoa().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Perfil de Jogador não encontrado."));

            return jogadorEquipeRepository.findContratoAtivo(jogador.getId())
                    .map(JogadorEquipe::getEquipe)
                    .orElseThrow(() -> new EntityNotFoundException("Jogador não está vinculado a nenhuma equipe ativa."));

        } else if ("ROLE_TECNICO".equals(role) || "ROLE_AUXILIAR".equals(role)) {
            ComissaoTecnica staff = comissaoTecnicaRepository.findByPessoaId(usuario.getPessoa().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Perfil de Comissão Técnica não encontrado."));

            return comissaoEquipeRepository.findContratoAtivo(staff.getId())
                    .map(ComissaoTecnicaEquipe::getEquipe)
                    .orElseThrow(() -> new EntityNotFoundException("Membro da comissão não está vinculado a nenhuma equipe ativa."));
        }

        throw new IllegalArgumentException("Seu perfil não possui uma equipe associada.");
    }

    public Equipe buscarPorId(Long id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada com id: " + id));
    }

    @Transactional
    public Equipe atualizar(Long id, EquipeDTO dto) {
        Equipe equipe = buscarPorId(id);
        equipe.setNome(dto.nome());
        equipe.setCidade(dto.cidade());
        equipe.setEstado(dto.estado());
        equipe.setEscudo(dto.escudo());
        equipe.setCorPrimaria(dto.corPrimaria());
        equipe.setApelido(dto.apelido());
        equipe.setDataFundacao(dto.dataFundacao());
        return equipeRepository.save(equipe);
    }

    public List<EquipeDTO> listarMeusTimes(String email) {
        Usuario usuario = usuarioRepository.findByPessoaEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        List<Equipe> equipes = equipeRepository.findAllByAdministradorId(usuario.getId());

        return equipes.stream()
                .map(equipe -> new EquipeDTO(
                        equipe.getId(),
                        equipe.getNome(),
                        equipe.getCidade(),
                        equipe.getEstado(),
                        equipe.getAdministrador().getId(),
                        equipe.getEscudo(),
                        equipe.getCorPrimaria(),
                        equipe.getApelido(),
                        equipe.getDataFundacao()
                ))
                .collect(Collectors.toList());
    }

    public EquipeStatsDTO buscarEstatisticas(Long equipeId) {
        List<Partida> partidas = partidaRepository.findHistoricoPorTime(equipeId);
        long vitorias = 0;
        long empates = 0;
        long derrotas = 0;
        long gp = 0;
        long gc = 0;

        for (Partida p : partidas) {
            if (p.getStatus() != PartidaStatus.FINALIZADA) continue;

            boolean isCasa = p.getEquipeCasa().getId().equals(equipeId);
            int golsTime = isCasa ? p.getPlacarCasa() : p.getPlacarVisitante();
            int golsAdv = isCasa ? p.getPlacarVisitante() : p.getPlacarCasa();

            gp += golsTime;
            gc += golsAdv;

            if (golsTime > golsAdv) vitorias++;
            else if (golsTime == golsAdv) empates++;
            else derrotas++;
        }

        long total = vitorias + empates + derrotas;
        double aproveitamento = total > 0 ? ((vitorias * 3.0 + empates) / (total * 3.0)) * 100.0 : 0.0;

        return new EquipeStatsDTO(
                total, vitorias, empates, derrotas, gp, gc, (gp - gc), aproveitamento
        );
    }
}