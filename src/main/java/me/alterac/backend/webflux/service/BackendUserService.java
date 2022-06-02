package me.alterac.backend.webflux.service;

import me.alterac.backend.webflux.entity.BackendUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BackendUserService {

    Mono<BackendUser> getByUsername(String username);

    Mono<BackendUser> createUser(String username, String password, String description);

    Mono<Void> updatePassword(String username, String password);

    Mono<Void> updateDescription(String username, String description);

    Mono<Page<BackendUser>> findByPage(int pageNum, int limit);
}
