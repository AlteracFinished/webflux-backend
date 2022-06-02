package me.alterac.backend.webflux.converter;


import me.alterac.backend.webflux.entity.BackendUser;
import me.alterac.backend.webflux.entity.BackendUserGetResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BackendUserToGetResponseConverter implements Converter<BackendUser, BackendUserGetResponse> {

    @Override
    public BackendUserGetResponse convert(BackendUser source) {
        return BackendUserGetResponse.builder()
                .username(source.getUsername())
                .description(source.getDescription())
                .build();
    }
}
