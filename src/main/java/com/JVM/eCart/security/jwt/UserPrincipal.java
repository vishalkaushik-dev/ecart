package com.JVM.eCart.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private Long id;
    private String email;
    private String password; // REQUIRED
    private List<SimpleGrantedAuthority> authorities;
    private Long sellerId;

    public UserPrincipal(Long id, String email, String password,
                         List<SimpleGrantedAuthority> authorities,
                         Long sellerId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.sellerId = sellerId;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getPassword() {   // ✅ FIXED
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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