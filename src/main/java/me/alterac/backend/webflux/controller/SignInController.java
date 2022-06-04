package me.alterac.backend.webflux.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.alterac.backend.webflux.entity.CommonResponse;
import me.alterac.backend.webflux.entity.SignInResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@RestController
public class SignInController {

    @PostMapping("/signIn")
    private Mono<CommonResponse<SignInResponse>> signIn(Authentication authentication, WebSession webSession) {
        UserDetails userDetails = ((UserDetails) authentication.getPrincipal());
        log.info("User {} signed in", userDetails.getUsername());
        return Mono.just(CommonResponse.createSuccessResponse(SignInResponse.builder()
                .sessionId(webSession.getId())
                .username(userDetails.getUsername())
                .build()));
    }

    @GetMapping("/signInFailed")
    private Mono<CommonResponse<Void>> signInFailed() {
        return Mono.just(CommonResponse.defaultFailedResponse());
    }

    @RequestMapping(value = "/signOut", method = {RequestMethod.GET, RequestMethod.POST})
    private Mono<CommonResponse<Void>> signOut() {
        return Mono.just(CommonResponse.defaultSuccessResponse());
    }
}
