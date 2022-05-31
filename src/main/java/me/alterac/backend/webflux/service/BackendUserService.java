package me.alterac.backend.webflux.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.alterac.backend.webflux.repository.BackendUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BackendUserService implements ReactiveUserDetailsService {
    private final @NonNull BackendUserRepository repository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return repository.findByUsername(username).map(backendUser -> {
            if (backendUser == null) {
                throw new UsernameNotFoundException(username);
            }
            return User.withUsername(username)
                    .password(backendUser.getPassword())
//                    .authorities(backendUser.getRoles()
//                            .stream()
//                            .map(SimpleGrantedAuthority::new)
//                            .collect(Collectors.toList()))
                    .roles(backendUser.getRoles())
                    .passwordEncoder((password) -> {
                        String combinedString = backendUser.getSalt() + password;
                        return DigestUtils.md5DigestAsHex(combinedString.getBytes(StandardCharsets.UTF_8));
                    })
                    .build();
        }).next();
    }
}
