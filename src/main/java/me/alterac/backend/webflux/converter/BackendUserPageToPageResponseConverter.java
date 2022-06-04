package me.alterac.backend.webflux.converter;

import lombok.AllArgsConstructor;
import me.alterac.backend.webflux.entity.BackendUser;
import me.alterac.backend.webflux.entity.BackendUserGetResponse;
import me.alterac.backend.webflux.entity.BackendUserPageResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BackendUserPageToPageResponseConverter implements Converter<Page<BackendUser>, BackendUserPageResponse> {

    private final BackendUserToGetResponseConverter conversionService;

    @Override
    public BackendUserPageResponse convert(Page<BackendUser> source) {
        return BackendUserPageResponse.builder()
                .userList(source.getContent()
                        .stream()
                        //.map(backendUser -> Objects.requireNonNull(conversionService.convert(backendUser)))
                        .map(backendUser -> BackendUserGetResponse.builder()
                                .id(backendUser.getId())
                                .username(backendUser.getUsername())
                                .description(backendUser.getDescription())
                                .roles(backendUser.getRoles())
                                .build())
                        .collect(Collectors.toList()))
                .total(source.getTotalElements())
                .build();
    }
}
