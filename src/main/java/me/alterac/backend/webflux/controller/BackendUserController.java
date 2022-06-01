package me.alterac.backend.webflux.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class BackendUserController {


    @GetMapping("/getCurrentUser")
    private Mono<Map<String, String>> getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Map<String, String> res = new HashMap<>();
        res.put("msg", "hello");
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                res.put("loginName", ((UserDetails) principal).getUsername());
            } else {
                throw new RuntimeException("Not authed with UserDetails.");
            }
        }
        return Mono.just(res);
    }
}
