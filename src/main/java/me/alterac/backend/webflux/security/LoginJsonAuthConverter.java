package me.alterac.backend.webflux.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.alterac.backend.webflux.entity.SignInRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class LoginJsonAuthConverter implements ServerAuthenticationConverter {

    private final ObjectMapper mapper;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getRequest().getBody()
                .next()
                .flatMap(buffer -> {
                    try {
                        SignInRequest request = mapper.readValue(buffer.asInputStream(), SignInRequest.class);
                        return Mono.just(request);
                    } catch (IOException e) {
                        log.debug("Can't read login request from JSON");
                        return Mono.error(e);
                    }
                })
                .map(request -> new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    }
}
