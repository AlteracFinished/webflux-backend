package me.alterac.backend.webflux.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("")
    private Mono<Map<String, String>> hello() {
        Map<String, String> res = new HashMap<>();
        res.put("msg", "hello");
        return Mono.just(res);
    }
}
