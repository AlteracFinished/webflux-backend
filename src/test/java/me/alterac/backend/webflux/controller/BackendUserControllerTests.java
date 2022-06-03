package me.alterac.backend.webflux.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.alterac.backend.webflux.entity.BackendUserCreateRequest;
import me.alterac.backend.webflux.entity.BackendUserPageResponse;
import me.alterac.backend.webflux.entity.CommonResponse;
import me.alterac.backend.webflux.entity.SignInResponse;
import me.alterac.backend.webflux.security.Roles;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.*;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
public class BackendUserControllerTests {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String currentSessionId;

//    @BeforeEach
    public void doSignIn() {
        log.info("Start web test.");

        Map<String, String> body = new HashMap<>();
        body.put("username", "admin");
        body.put("password", "123456");

        CommonResponse<SignInResponse> response = webClient.post()
                .uri("/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().value("SESSION", sessionId -> {
                    currentSessionId = sessionId;
                    log.info("sessionId: {}", currentSessionId);
                })
                .returnResult(new ParameterizedTypeReference<CommonResponse<SignInResponse>>() {})
                .getResponseBody()
                .blockFirst();

        assertNotNull(response);

        currentSessionId = response.getData().getSessionId();
        log.info("sessionId: {}", currentSessionId);

    }

    @Test
    @WithMockUser(username = "admin", roles = Roles.ADMIN)
    public void page() {
        CommonResponse<BackendUserPageResponse> response = webClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/user/findByPage")
                                .queryParam("pageNum", 0)
                                .queryParam("limit", 10)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<CommonResponse<BackendUserPageResponse>>() {})
                .getResponseBody()
                .blockFirst();

        log.info("response {}", response);
    }

    @Test
    public void signIn() {

    }

    @Test
    public void signInFailed() {

        webClient.get()
                .uri("/signInFailed")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
}
