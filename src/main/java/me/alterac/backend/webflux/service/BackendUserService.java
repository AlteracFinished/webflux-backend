package me.alterac.backend.webflux.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.alterac.backend.webflux.repository.BackendUserRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
                    .roles(backendUser.getRoles().toArray(new String[0]))
                    .build();
        }).next();
    }
}
