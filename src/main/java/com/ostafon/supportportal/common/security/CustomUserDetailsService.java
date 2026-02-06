package com.ostafon.supportportal.common.security;

import com.ostafon.supportportal.users.model.UserEntity;
import com.ostafon.supportportal.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email)
            throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (user.getIsActive() == null || !user.getIsActive()) {
            throw new UsernameNotFoundException("User account is disabled: " + email);
        }

        return new CustomUserDetails(user);
    }
}
