package me.alterac.backend.webflux.controller;

import lombok.AllArgsConstructor;
import me.alterac.backend.webflux.entity.SignInRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/signIn")
public class SignInController {

    private final ReactiveAuthenticationManager authenticationManager;

    private final ServerSecurityContextRepository securityContextRepository;

    @PostMapping("")
    private Mono<Void> signIn(@RequestBody SignInRequest request, ServerWebExchange webExchange) {
        return Mono.just(request)
                .flatMap(form -> {
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            form.getUsername(),
                            form.getPassword()
                    );

                    return authenticationManager
                            .authenticate(token)
                            .doOnError(err -> {
                                System.out.println(err.getMessage());
                            })
                            .flatMap(authentication -> {
                                SecurityContextImpl securityContext = new SecurityContextImpl(authentication);

                                return securityContextRepository
                                        .save(webExchange, securityContext)
                                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                            });
                });
    }
}
