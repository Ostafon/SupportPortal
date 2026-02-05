package com.ostafon.supportportal.common.security;



import com.ostafon.supportportal.users.model.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record CustomUserDetails(UserEntity user) implements UserDetails {

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + Optional.ofNullable(user.getRole())));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public @NonNull String getUsername() {
        return Optional.ofNullable(user.getUsername()).orElse("");
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

    public String getRole() {
        return user.getRole().name();
    }

    public Long getId() {
        return user.getId();
    }
}