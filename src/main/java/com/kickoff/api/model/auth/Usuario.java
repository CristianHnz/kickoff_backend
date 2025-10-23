package com.kickoff.api.model.auth;

import com.kickoff.api.model.core.Pessoa;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false, fetch = FetchType.EAGER) // EAGER é importante para carregar a Pessoa junto
    @JoinColumn(name = "pessoa_id", unique = true, nullable = false)
    private Pessoa pessoa;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role; // Ex: "ROLE_ADMIN", "ROLE_JOGADOR"
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCadastro;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.pessoa.getEmail();
    }
}