package com.devsenior.soledad.reservas_backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtPrincipal implements UserDetails {
    private final String username;
    private final Long companyId;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtPrincipal(String username, Long companyId, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.companyId = companyId;
        this.authorities = authorities;
    }

    public Long getCompanyId() {
        return companyId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

