package com.kickoff.api.service;

import com.kickoff.api.dto.role.ContratacaoStaffDTO;
import com.kickoff.api.dto.role.StaffResumoDTO;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.relationship.ComissaoTecnicaEquipe;
import com.kickoff.api.model.role.ComissaoTecnica;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.relationship.ComissaoTecnicaEquipeRepository;
import com.kickoff.api.repository.role.ComissaoTecnicaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComissaoTecnicaService {

    @Autowired
    private ComissaoTecnicaRepository comissaoRepository;
    @Autowired
    private ComissaoTecnicaEquipeRepository comissaoEquipeRepository;
    @Autowired
    private EquipeRepository equipeRepository;

    public List<StaffResumoDTO> listarDaEquipe(Long equipeId) {
        return comissaoEquipeRepository.findAtivosByEquipeId(equipeId).stream()
                .map(vinculo -> mapToDTO(vinculo.getComissaoTecnica(), "CONTRATADO"))
                .collect(Collectors.toList());
    }

    public List<StaffResumoDTO> listarDisponiveis() {
        return comissaoRepository.findComissaoSemContrato().stream()
                .map(ct -> mapToDTO(ct, "LIVRE"))
                .collect(Collectors.toList());
    }

    @Transactional
    public void contratarStaff(Long equipeId, ContratacaoStaffDTO dto) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada"));

        ComissaoTecnica staff = comissaoRepository.findById(dto.comissaoId())
                .orElseThrow(() -> new EntityNotFoundException("Membro da comissão técnica não encontrado"));

        if (comissaoEquipeRepository.findContratoAtivo(staff.getId()).isPresent()) {
            throw new IllegalArgumentException("Este membro já possui um contrato ativo.");
        }

        ComissaoTecnicaEquipe vinculo = new ComissaoTecnicaEquipe();
        vinculo.setEquipe(equipe);
        vinculo.setComissaoTecnica(staff);
        vinculo.setDataEntrada(LocalDate.now());

        comissaoEquipeRepository.save(vinculo);
    }

    @Transactional
    public void dispensarStaff(Long equipeId, Long comissaoId) {
        ComissaoTecnicaEquipe vinculoAtivo = comissaoEquipeRepository.findContratoAtivo(comissaoId)
                .orElseThrow(() -> new EntityNotFoundException("Este membro não possui um vínculo ativo."));

        if (!vinculoAtivo.getEquipe().getId().equals(equipeId)) {
            throw new IllegalArgumentException("Você não pode dispensar um membro que não é da sua equipe.");
        }

        vinculoAtivo.setDataSaida(LocalDate.now());
        comissaoEquipeRepository.save(vinculoAtivo);
    }

    private StaffResumoDTO mapToDTO(ComissaoTecnica ct, String status) {
        return new StaffResumoDTO(
                ct.getId(),
                ct.getPessoa().getNome(),
                ct.getFuncao(),
                status
        );
    }

    public List<StaffResumoDTO> listarTodos() {
        List<ComissaoTecnica> todos = comissaoRepository.findAll();

        return todos.stream().map(ct -> {
            String status = comissaoEquipeRepository.findContratoAtivo(ct.getId())
                    .isPresent() ? "CONTRATADO" : "LIVRE";
            return mapToDTO(ct, status);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void atualizarFuncao(Long comissaoId, String novaFuncao) {
        if (!"TECNICO".equals(novaFuncao) && !"AUXILIAR".equals(novaFuncao)) {
            throw new IllegalArgumentException("Função inválida. Use 'TECNICO' ou 'AUXILIAR'.");
        }

        ComissaoTecnica staff = comissaoRepository.findById(comissaoId)
                .orElseThrow(() -> new EntityNotFoundException("Membro da comissão não encontrado"));

        staff.setFuncao(novaFuncao);
        comissaoRepository.save(staff);
    }

    @Transactional
    public void deletarMembro(Long comissaoId) {
        if (comissaoEquipeRepository.findContratoAtivo(comissaoId).isPresent()) {
            throw new IllegalArgumentException("Não é possível excluir. Este membro está contratado por uma equipe. Dispense-o primeiro.");
        }

        ComissaoTecnica staff = comissaoRepository.findById(comissaoId)
                .orElseThrow(() -> new EntityNotFoundException("Membro da comissão não encontrado"));

        List<ComissaoTecnicaEquipe> historico = comissaoEquipeRepository.findByComissaoTecnicaId(comissaoId);
        comissaoEquipeRepository.deleteAll(historico);

        comissaoRepository.delete(staff);
    }
}