package me.alterac.backend.webflux.controller;

import lombok.AllArgsConstructor;
import me.alterac.backend.webflux.entity.*;
import me.alterac.backend.webflux.service.BackendUserService;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import javax.validation.constraints.Positive;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class BackendUserController {

    private final ReactiveRedisSessionRepository reactiveRedisSessionRepository;

    private final BackendUserService backendUserService;

    private final ConversionService conversionService;

    @GetMapping("/getCurrentUser")
    private Mono<CommonResponse<BackendUserGetResponse>> getCurrentUser(Authentication authentication) {
        UserDetails userDetails = ((UserDetails) authentication.getPrincipal());
        return backendUserService.getByUsername(userDetails.getUsername())
                .map(backendUser ->
                        Objects.requireNonNull(conversionService.convert(backendUser, BackendUserGetResponse.class)))
                .map(CommonResponse::createSuccessResponse);
    }

    @GetMapping("/countSignedUser")
    private Mono<CommonResponse<Long>> countSignedUser() {
        return reactiveRedisSessionRepository.getSessionRedisOperations()
                .scan(ScanOptions.scanOptions()
                        .match(ReactiveRedisSessionRepository.DEFAULT_NAMESPACE + ":sessions:*")
                        .build()
                )
                .flatMap(s ->
                        reactiveRedisSessionRepository.getSessionRedisOperations().opsForHash().entries(s)
                                .map(objectObjectEntry -> Tuples.of(s, objectObjectEntry)))
                .filter(rule -> {
                    Map.Entry<Object, Object> t = rule.getT2();
                    String key = "sessionAttr:securityContext";
                    if (key.equals(t.getKey())) {
                        SecurityContextImpl sci = ((SecurityContextImpl) t.getValue());
                        return sci != null;
                    }
                    return false;
                })
                .count()
                .map(CommonResponse::createSuccessResponse);
    }

    @PutMapping("/create")
    private Mono<CommonResponse<Void>> create(@RequestBody BackendUserCreateRequest request) {
        return backendUserService.createUser(
                request.getUsername(), request.getPassword(), request.getDescription()
        ).thenReturn(CommonResponse.defaultSuccessResponse());
    }

    @PostMapping("/updatePassword")
    private Mono<CommonResponse<Void>> updatePassword(
            @RequestBody BackendUserPasswordUpdateRequest request
    ) {
        return backendUserService.updatePassword(request.getUsername(), request.getPassword())
                .thenReturn(CommonResponse.defaultSuccessResponse());
    }

    @PostMapping("/updateDescription")
    private Mono<CommonResponse<Void>> updateDescription(
            @RequestBody BackendUserUpdateRequest request
    ) {
        return backendUserService.updateDescription(request.getUsername(), request.getDescription())
                .thenReturn(CommonResponse.defaultSuccessResponse());
    }

    @GetMapping("/findByPage")
    @Validated
    private Mono<CommonResponse<BackendUserPageResponse>> findByPage(
            @Positive @RequestParam("pageNum") Integer pageNum,
            @Positive @RequestParam("limit") Integer limit
    ) {
        return backendUserService.findByPage(pageNum - 1, limit)
                .map(page -> (BackendUserPageResponse) Objects.requireNonNull(conversionService.convert(
                        page,
                        new TypeDescriptor(
                                ResolvableType.forClassWithGenerics(Page.class, BackendUser.class),
                                Page.class,
                                null
                        ),
                        TypeDescriptor.valueOf(BackendUserPageResponse.class))))
                .map(CommonResponse::createSuccessResponse);
    }
}
