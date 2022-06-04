package me.alterac.backend.webflux.controller;

import lombok.extern.slf4j.Slf4j;
import me.alterac.backend.webflux.entity.CommonResponse;
import me.alterac.backend.webflux.entity.SignInResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
public class SignInControllerTests {

    @Autowired
    private WebTestClient webClient;

    @Test
    public void signInTest() {
        log.info("Start web test.");

        Map<String, String> body = new HashMap<>();
        body.put("username", "admin");
        body.put("password", "123456");

        webClient.post()
                .uri("/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CommonResponse<SignInResponse>>() {});
    }
}
