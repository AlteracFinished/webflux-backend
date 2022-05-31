package me.alterac.backend.webflux.configuration;

import me.alterac.backend.webflux.service.BackendUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfiguration {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            BackendUserService backendUserService
    ) {
        http
                .csrf().disable()
                .authorizeExchange()
                // .pathMatchers(HttpMethod.POST, "/user/*").hasRole(ADMIN)
                .pathMatchers("/hello").permitAll()
                .and()
                .httpBasic()
                .and()
                .formLogin()
                .authenticationManager(new UserDetailsRepositoryReactiveAuthenticationManager(backendUserService));
        return http.build();
    }
}
