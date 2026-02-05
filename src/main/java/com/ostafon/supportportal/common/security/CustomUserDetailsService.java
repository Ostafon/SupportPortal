package com.ostafon.supportportal.common.security;


import com.ostafon.supportportal.users.model.UserEntity;
import com.ostafon.supportportal.users.repo.UserRepo;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepository;

    public CustomUserDetailsService(UserRepo userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username)
            throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found:" + username));
        return new CustomUserDetails(user);
    }
}
