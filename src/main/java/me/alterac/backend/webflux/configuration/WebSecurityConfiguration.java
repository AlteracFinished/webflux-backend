package me.alterac.backend.webflux.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.alterac.backend.webflux.security.LoginJsonAuthConverter;
import me.alterac.backend.webflux.security.Roles;
import me.alterac.backend.webflux.security.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.session.HeaderWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;

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
    public WebSessionIdResolver webSessionIdResolver() {
        HeaderWebSessionIdResolver resolver = new HeaderWebSessionIdResolver();
        resolver.setHeaderName("SESSION");
        return resolver;
    }

    @Bean
    public CorsConfigurationSource configurationSource(@Value("${allowed-origin:*}") String allowedOrigin) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Collections.singletonList(allowedOrigin));
        corsConfig.setMaxAge(8000L);
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

//    @Bean
    public WebExceptionHandler exceptionHandler() {
        return (ServerWebExchange exchange, Throwable ex) -> {
            if (ex instanceof IllegalArgumentException) {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return exchange.getResponse().setComplete();
            }
            return Mono.error(ex);
        };
    }

    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler(
            @Value("${print-stacktrace:true}") Boolean printStackTrace,
            ErrorAttributes errorAttributes,
            WebProperties.Resources resources,
            ApplicationContext applicationContext
    ) {
        return new GlobalExceptionHandler(errorAttributes, resources, applicationContext, printStackTrace);
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            UserDetailsRepositoryReactiveAuthenticationManager authenticationManager,
            AuthenticationWebFilter authenticationWebFilter,
            CorsConfigurationSource configurationSource
    ) {
        RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/signOut"));

        http.csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, "/signIn").authenticated()
                .pathMatchers(HttpMethod.OPTIONS, "/signIn").permitAll()
                .pathMatchers("/signInFailed", "/signOut").permitAll()
                .pathMatchers("/user/getCurrentUser").hasAnyRole(Roles.ADMIN, Roles.USER)
                .pathMatchers("/user/**").hasAnyRole(Roles.ADMIN)
                .anyExchange()
                .authenticated()
                .and()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository())
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .logout()
                .logoutUrl("/signOut")
                .logoutSuccessHandler(logoutSuccessHandler)
                .and()
                .cors().configurationSource(configurationSource);
        return http.build();
    }
}
