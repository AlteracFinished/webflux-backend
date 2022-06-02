package me.alterac.backend.webflux.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.alterac.backend.webflux.security.LoginJsonAuthConverter;
import me.alterac.backend.webflux.security.Roles;
import me.alterac.backend.webflux.service.BackendUserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(proxyTargetClass = true)
public class WebSecurityConfiguration {

    @Bean
    PasswordEncoder defaultPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    ServerAuthenticationConverter serverAuthenticationConverter(
            ObjectMapper objectMapper
    ) {
        return new LoginJsonAuthConverter(objectMapper);
    }

    @Bean
    public UserDetailsRepositoryReactiveAuthenticationManager authenticationManager(
            ReactiveUserDetailsService reactiveUserDetailsService
    ) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager
                = new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
        authenticationManager.setPasswordEncoder(defaultPasswordEncoder());
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
        filter.setServerAuthenticationConverter(serverAuthenticationConverter(objectMapper));
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/signIn")
        );
        filter.setAuthenticationFailureHandler(new RedirectServerAuthenticationFailureHandler("/signInFailed"));

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
                .authorizeExchange()
                .pathMatchers("/signIn").authenticated()
                .pathMatchers("/hello/**", "/signInFailed", "/signOut").permitAll()
                .pathMatchers("/user/**").hasAnyRole(Roles.ADMIN, Roles.USER)
                .anyExchange()
                .denyAll()
                .and()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository())
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .logout()
                .logoutUrl("/signOut");
        return http.build();
    }
}
