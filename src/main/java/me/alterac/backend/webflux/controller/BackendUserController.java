package me.alterac.backend.webflux.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.alterac.backend.webflux.entity.BackendUserCreateRequest;
import me.alterac.backend.webflux.entity.BackendUserGetResponse;
import me.alterac.backend.webflux.entity.BackendUserPageResponse;
import me.alterac.backend.webflux.entity.CommonResponse;
import me.alterac.backend.webflux.repository.BackendUserRepository;
import me.alterac.backend.webflux.service.BackendUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .map(backendUser -> conversionService.convert(backendUser, BackendUserGetResponse.class))
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
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        return backendUserService.updatePassword(username, password)
                .thenReturn(CommonResponse.defaultSuccessResponse());
    }

    @PostMapping("/updateDescription")
    private Mono<CommonResponse<Void>> updateDescription(
            @RequestParam("username") String username,
            @RequestParam("description") String description
    ) {
        return backendUserService.updateDescription(username, description)
                .thenReturn(CommonResponse.defaultSuccessResponse());
    }

//    @GetMapping("/findByPage")
//    private Mono<CommonResponse<BackendUserPageResponse>> findByPage(
//             @PositiveOrZero(message = "pageNum") @RequestParam("pageNum") Integer pageNum,
//             @Positive(message = "limit") @RequestParam("limit") Integer limit
//    ) {
//        return backendUserService.findByPage(pageNum, limit)
//                .flatMap(page -> {
//                    return BackendUserPageResponse.builder()
//                            .userList(page.getContent().stream().map())
//                            .total(page.getTotalPages())
//                            .build()
//                })
//    }
}
