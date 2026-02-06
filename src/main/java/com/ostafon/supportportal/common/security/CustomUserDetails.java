package com.ostafon.supportportal.common.security;

import com.ostafon.supportportal.users.model.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record CustomUserDetails(UserEntity user) implements UserDetails {

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security автоматически добавляет ROLE_ префикс при проверке hasRole()
        // Но для hasAuthority() используем без префикса
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public @NonNull String getUsername() {
        return user.getUsername(); // теперь возвращает email
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
        return user.getIsActive() != null && user.getIsActive();
    }

    public String getRole() {
        return user.getRole().name();
    }

    public Long getId() {
        return user.getId();
    }
}