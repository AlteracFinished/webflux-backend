package me.alterac.backend.webflux.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class BackendUserController {

    private final ObjectMapper objectMapper;

    @GetMapping("/getCurrentUser")
    private Mono<Map<String, String>> getCurrentUser(WebSession webSession) throws JsonProcessingException {
        Map<String, String> res = new HashMap<>();
        res.put("msg", "hello");
        if (webSession != null) {
            res.put("session", objectMapper.writeValueAsString(webSession.getAttributes()));
        }
        return Mono.just(res);
    }
}
