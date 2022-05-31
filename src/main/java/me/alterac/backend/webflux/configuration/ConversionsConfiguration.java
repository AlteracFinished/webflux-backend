package me.alterac.backend.webflux.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.alterac.backend.webflux.converter.ListToStringConverter;
import me.alterac.backend.webflux.converter.StringToListConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.MySqlDialect;

@AllArgsConstructor
@Configuration
public class ConversionsConfiguration {

    private final ObjectMapper objectMapper;

    @Bean
    public R2dbcCustomConversions redisCustomConversions(){
        return R2dbcCustomConversions.of(MySqlDialect.INSTANCE, new ListToStringConverter(objectMapper), new StringToListConverter(objectMapper));
    }
}
