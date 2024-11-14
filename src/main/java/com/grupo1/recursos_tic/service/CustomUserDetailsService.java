package com.grupo1.recursos_tic.service;

import com.grupo1.recursos_tic.model.User;
import com.grupo1.recursos_tic.model.UserRole;
import com.grupo1.recursos_tic.repository.UserRepo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService extends UserDetailsServiceImpl {

    private final UserRepo userRepository;
    private static final UserRole[] USER_ROLES = UserRole.values();

    public CustomUserDetailsService(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserRole userRole = user.getRole(); // Asumimos que tienes un método getRole() en tu User entity

        // Convertir el UserRole enum a SimpleGrantedAuthority
        Collection<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(userRole.name())
        );

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(Collections.singletonList(userRole.values())
                .authorities(authorities)
                .build();
    }

}

