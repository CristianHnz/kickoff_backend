package com.kickoff.api.service;

import com.kickoff.api.dto.core.EquipeDTO;
import com.kickoff.api.model.auth.Usuario;
import com.kickoff.api.model.core.Equipe;
import com.kickoff.api.model.role.Jogador;
import com.kickoff.api.repository.auth.UsuarioRepository;
import com.kickoff.api.repository.core.EquipeRepository;
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

        return equipeRepository.save(equipe);
    }

    public List<EquipeDTO> listarTodas() {
        return equipeRepository.findAll().stream()
                .map(equipe -> new EquipeDTO(
                        equipe.getId(),
                        equipe.getNome(),
                        equipe.getCidade(),
                        equipe.getEstado()
                ))
                .collect(Collectors.toList());
    }

    public Equipe buscarEquipeDoGestor(String email) {

        Usuario gestor = usuarioRepository.findByPessoaEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário gestor não encontrado"));

        return equipeRepository.findByAdministradorId(gestor.getId())
                .orElseThrow(() -> new EntityNotFoundException("Nenhuma equipe encontrada para este gestor."));
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
        return equipeRepository.save(equipe);
    }

//    @Transactional(readOnly = true)
//    public List<Equipe> buscarEquipesPorAdministrador(Usuario administrador) {
//        return equipeRepository.findByAdministrador(administrador);
//    }

//    @Transactional(readOnly = true)
//    public Optional<Equipe> buscarEquipePorIdEAdministrador(Long id, Usuario administrador) {
//        return equipeRepository.findByIdAndAdministrador(id, administrador);
//    }

//    @Transactional
//    public Equipe atualizarEquipe(Long id, EquipeDTO dto, Usuario administrador) {
//        Equipe equipeExistente = equipeRepository.findByIdAndAdministrador(id, administrador)
//                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada ou você não tem permissão para editá-la."));
//
//        if (equipeRepository.findByNomeAndIdNot(dto.nome(), id).isPresent()) {
//            throw new IllegalArgumentException("O nome '" + dto.nome() + "' já está em uso por outra equipe.");
//        }
//        equipeExistente.setNome(dto.nome());
//        equipeExistente.setCidade(dto.cidade());
//        equipeExistente.setEstado(dto.estado());
//
//        return equipeRepository.save(equipeExistente);
//    }

//    @Transactional
//    public void deletarEquipe(Long id, Usuario administrador) {
//        Equipe equipe = equipeRepository.findByIdAndAdministrador(id, administrador)
//                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada ou você não tem permissão para excluí-la."));
//        equipeRepository.delete(equipe);
//    }

//    @Transactional
//    public void vincularJogadorSemEquipe(Long equipeId, Long jogadorId, Usuario administrador) {
//        Equipe equipe = buscarEquipePorIdEAdministrador(equipeId, administrador)
//                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada para este administrador."));
//
////        Jogador jogador = jogadorRepository.findByIdAndEquipeIsNull(jogadorId)
////                .orElseThrow(() -> new EntityNotFoundException("Jogador não encontrado ou já possui equipe."));
//
////        if (jogador.getEquipe() != null) {
////            throw new IllegalArgumentException("Este jogador já está vinculado a uma equipe.");
////        }
////        jogador.setEquipe(equipe);
////        jogadorRepository.save(jogador);
//    }

//    public List<Equipe> listarTodas() {
//        return equipeRepository.findAll();
//    }

}