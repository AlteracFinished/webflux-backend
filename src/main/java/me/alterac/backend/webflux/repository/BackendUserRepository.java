package me.alterac.backend.webflux.repository;

import me.alterac.backend.webflux.entity.BackendUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BackendUserRepository extends R2dbcRepository<BackendUser, Long> {

    @Modifying
    @Query("update `backend_user` set description = :description where username = :username")
    Mono<Integer> updateDescriptionByUsername(String description, String username);

    @Modifying
    @Query("update `backend_user` set password = :password where username = :username")
    Mono<Integer> updatePasswordByUsername(String password, String username);

    Mono<BackendUser> findByUsername(String username);

    Flux<BackendUser> findAllBy(Pageable pageable);
}
