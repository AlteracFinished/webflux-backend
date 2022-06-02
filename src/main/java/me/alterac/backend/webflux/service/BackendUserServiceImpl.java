package me.alterac.backend.webflux.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.alterac.backend.webflux.entity.BackendUser;
import me.alterac.backend.webflux.repository.BackendUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class BackendUserServiceImpl implements ReactiveUserDetailsService, BackendUserService {
    private final @NonNull BackendUserRepository repository;

    private final @NonNull PasswordEncoder passwordEncoder;

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
        });
    }

    @Override
    public Mono<BackendUser> getByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public Mono<BackendUser> createUser(String username, String password, String description) {
        return repository.save(
                BackendUser.builder()
                        .username(username)
                        .password(passwordEncoder.encode(password))
                        .description(description)
                        .build()
        );
    }

    @Override
    public Mono<Void> updatePassword(String username, String password) {
        return repository.updatePasswordByUsername(passwordEncoder.encode(password), username).then();
    }

    @Override
    public Mono<Void> updateDescription(String username, String description) {
        return repository.updateDescriptionByUsername(description, username).then();
    }

    @Override
    public Mono<Page<BackendUser>> findByPage(int pageNum, int limit) {
        PageRequest pageable = PageRequest.of(pageNum, limit)
                .withSort(Sort.by("id").descending());
        return repository.findAll(pageable);
    }
}
