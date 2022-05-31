package me.alterac.backend.webflux.repository;

import me.alterac.backend.webflux.entity.BackendUser;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BackendUserRepository extends R2dbcRepository<BackendUser, Long> {

//    @Query("select id, `username`, `password`, salt, description, login_at, roles from `user` u " +
//            "where u.username = :username")
//    Flux<BackendUser> loadByUsername(String username);
//
//    @Modifying
//    @Query("update `user` set description = :description where username = :username")
//    Mono<Integer> updateDescriptionByUsername(String description, String username);

    Flux<BackendUser> findByUsername(String username);
}
