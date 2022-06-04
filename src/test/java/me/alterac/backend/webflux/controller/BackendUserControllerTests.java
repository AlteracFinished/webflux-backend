package me.alterac.backend.webflux.controller;


import lombok.extern.slf4j.Slf4j;
import me.alterac.backend.webflux.entity.BackendUserCreateRequest;
import me.alterac.backend.webflux.entity.BackendUserPageResponse;
import me.alterac.backend.webflux.entity.CommonResponse;
import me.alterac.backend.webflux.security.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
public class BackendUserControllerTests {

    @Autowired
    private WebTestClient webClient;

    @Test
    @WithMockUser(username = "admin", roles = Roles.ADMIN)
    public void page() {
        CommonResponse<BackendUserPageResponse> response = webClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/user/findByPage")
                                .queryParam("pageNum", 1)
                                .queryParam("limit", 10)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<CommonResponse<BackendUserPageResponse>>() {})
                .getResponseBody()
                .blockFirst();

        log.info("response {}", response);
        assertNotNull(response);
        assertEquals(response.getMsg(), "success");
    }

    @Test
    @WithMockUser(username = "admin", roles = Roles.ADMIN)
    public void createUser() {
        BackendUserCreateRequest request = new BackendUserCreateRequest();
        request.setUsername("userForTest-" + UUID.randomUUID().toString());
        request.setPassword("1234");
        request.setDescription(UUID.randomUUID().toString());

        CommonResponse<String> response = webClient
                .put()
                .uri("/user/create")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<CommonResponse<String>>() {})
                .getResponseBody()
                .blockFirst();

        log.info("response {}", response);
        assertNotNull(response);
        assertEquals(response.getMsg(), "success");
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
