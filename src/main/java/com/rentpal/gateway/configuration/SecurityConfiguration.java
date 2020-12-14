package com.rentpal.gateway.configuration;

import com.rentpal.gateway.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.stereotype.Component;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;

/**
 * @author frank
 * @created 06 Dec,2020 - 6:24 PM
 * This class contains web flux security configurations
 */

@Component
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Value("${angular}")
    private String angularUrl;

    private final AccountService accountService;

    /**
     * Instantiates a new Security configuration.
     *
     * @param accountService the account service
     */
    @Autowired
    public SecurityConfiguration(AccountService accountService){
        this.accountService=accountService;
    }

    /**
     * Security configuration for federated login using google and facebook.
     * Right now, csrf is disabled as login request is from angular and csrf token is not set as origin is different.
     *
     * @param http the http
     * @return the security web filter chain
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            //disabling CSRF
            .csrf().disable()
            //.csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
            .cors()
                .configurationSource(corsConfigurationSource())
        .and()
            .exceptionHandling()
                .authenticationEntryPoint((ServerWebExchange exchange, AuthenticationException exception)-> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .accessDeniedHandler((ServerWebExchange exchange, AccessDeniedException exception)-> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
        .and()
            .authorizeExchange()
            .anyExchange()
            .authenticated()
        .and()
            .formLogin()
        .and()
            .oauth2Login()
                .authenticationSuccessHandler((WebFilterExchange webFilterExchange, Authentication auth)->{
                    accountService.addOAuthUser(auth);
                    ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();
                    ServerWebExchange exchange = webFilterExchange.getExchange();
                    // redirecting to angular server after successful authentication
                    return redirectStrategy.sendRedirect(exchange, URI.create(angularUrl));
                })
        .and()
            .logout().logoutUrl("/logout");
        return http.build();
    }

    /**
     * CORS configuration for angular web application.
     *
     * @return the cors configuration source
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(angularUrl));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS"));
        configuration.applyPermitDefaultValues();
        configuration.setAllowCredentials(true);
        //the below three lines will add the relevant CORS response headers
        configuration.addAllowedOrigin(angularUrl);
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
