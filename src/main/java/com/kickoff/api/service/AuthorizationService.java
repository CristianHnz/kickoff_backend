package com.kickoff.api.service;

import com.kickoff.api.repository.core.PessoaRepository;
import com.kickoff.api.model.auth.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = pessoaRepository.findByEmail(username)
                .map(pessoa -> pessoa.getUsuario())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + username));
        return usuario;
    }
}