package me.alterac.backend.webflux.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.alterac.backend.webflux.security.LoginJsonAuthConverter;
import me.alterac.backend.webflux.service.BackendUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfiguration {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    @Bean
    public UserDetailsRepositoryReactiveAuthenticationManager authenticationManager(
            BackendUserService backendUserService
    ) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager
                = new UserDetailsRepositoryReactiveAuthenticationManager(backendUserService);
        authenticationManager.setPasswordEncoder(new BCryptPasswordEncoder());
        return authenticationManager;
    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        WebSessionServerSecurityContextRepository securityContextRepository =
                new WebSessionServerSecurityContextRepository();

        securityContextRepository.setSpringSecurityContextAttrName("securityContext");

        return securityContextRepository;
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter(
            ObjectMapper objectMapper,
            UserDetailsRepositoryReactiveAuthenticationManager authenticationManager
    ) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager);

        filter.setSecurityContextRepository(securityContextRepository());
        filter.setServerAuthenticationConverter(new LoginJsonAuthConverter(objectMapper));
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/signIn")
        );

        return filter;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            UserDetailsRepositoryReactiveAuthenticationManager authenticationManager,
            AuthenticationWebFilter authenticationWebFilter
    ) {
        http.csrf().disable()
                .cors().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository())
                .authorizeExchange()
                .pathMatchers("/user/getCurrentUser").hasRole("ADMIN")
                .pathMatchers("/**", "/login", "/logout", "/hello").permitAll()
                .anyExchange().permitAll()
                .and()
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }
}
