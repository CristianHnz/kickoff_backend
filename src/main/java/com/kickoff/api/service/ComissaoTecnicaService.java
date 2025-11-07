package com.kickoff.api.service;

import com.kickoff.api.dto.role.ComissaoTecnicaDTO;
import com.kickoff.api.dto.role.ComissaoTecnicaResponseDTO;
import com.kickoff.api.mapper.ComissaoTecnicaMapper; // Você precisa criar/usar este
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.core.Pessoa;
import com.kickoff.api.model.role.ComissaoTecnica;
import com.kickoff.api.repository.core.EquipeRepository;
import com.kickoff.api.repository.core.PessoaRepository;
import com.kickoff.api.repository.role.ComissaoTecnicaRepository; // Você precisa criar este
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ComissaoTecnicaService {

    @Autowired
    private ComissaoTecnicaRepository comissaoTecnicaRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private ComissaoTecnicaMapper mapper;
    @Autowired
    private PessoaRepository pessoaRepository;

    @Transactional(readOnly = true)
    public List<ComissaoTecnicaResponseDTO> listarComissaoPorEquipe(Long equipeId, Usuario administrador) {
        Equipe equipe = equipeRepository.findByIdAndAdministrador(equipeId, administrador)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada ou você não tem permissão sobre ela."));
        List<ComissaoTecnica> comissao = comissaoTecnicaRepository.findByEquipeId(equipe.getId());
        return mapper.toResponseDTOList(comissao);
    }

    @Transactional
    public ComissaoTecnica criarMembroComissao(Long equipeId, ComissaoTecnicaDTO dto, Usuario administrador) {
        Equipe equipe = equipeRepository.findByIdAndAdministrador(equipeId, administrador)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada ou você não tem permissão sobre ela."));

        Pessoa pessoa = pessoaRepository.findByEmail(dto.emailPessoa())
                .orElseThrow(() -> new EntityNotFoundException("Nenhuma pessoa encontrada com o email: " + dto.emailPessoa()));

        comissaoTecnicaRepository.findByPessoa(pessoa).ifPresent(membro -> {
            throw new IllegalArgumentException("Esta pessoa já está cadastrada na comissão técnica da equipe: " + membro.getEquipe().getNome());
        });

        ComissaoTecnica novoMembro = new ComissaoTecnica();
        novoMembro.setPessoa(pessoa);
        novoMembro.setEquipe(equipe);
        novoMembro.setFuncao(dto.funcao());
        novoMembro.setDataEntradaEquipe(LocalDate.now()); // Define a data de entrada

        return comissaoTecnicaRepository.save(novoMembro);
    }
}