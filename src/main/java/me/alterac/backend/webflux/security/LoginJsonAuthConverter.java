package me.alterac.backend.webflux.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.alterac.backend.webflux.entity.SignInRequest;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.ByteBuffer;

@Slf4j
@AllArgsConstructor
public class LoginJsonAuthConverter implements ServerAuthenticationConverter {

    private final ObjectMapper mapper;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getRequest().getBody()
                .next()
                .flatMap(buffer -> {
                    // Avoid get body twice
                    byte[] dst;
                    if (buffer instanceof DefaultDataBuffer) {
                        ByteBuffer copy = ((DefaultDataBuffer) buffer).getNativeBuffer().duplicate();
                        dst = new byte[copy.remaining()];
                        copy.get(dst);
                    } else if (buffer instanceof NettyDataBuffer) {
                        ByteBuf copy = ((NettyDataBuffer) buffer).getNativeBuffer().copy();
                        dst = new byte[copy.capacity()];
                        copy.readBytes(dst);
                    } else {
                        return Mono.error(new IllegalStateException("DataBuffer not supported"));
                    }
                    try {
                        return Mono.just(mapper.readValue(dst, SignInRequest.class));
                    } catch (IOException e) {
                        log.error("Can't read login request from JSON");
                        return Mono.error(e);
                    }
                })
                .map(request -> new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    }
}
