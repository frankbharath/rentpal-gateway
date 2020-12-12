package com.rentpal.gateway.configuration;

import com.rentpal.gateway.model.SpringUserDetails;
import com.rentpal.gateway.model.User;
import com.rentpal.gateway.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;

/*
 * @author frank
 * @created 06 Dec,2020 - 6:24 PM
 */

@Component
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Value("${angular}")
    private String angularUrl;

    private final AccountService accountService;

    @Autowired
    public SecurityConfiguration(AccountService accountService){
        this.accountService=accountService;
    }
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
            .cors()
                .configurationSource(corsConfigurationSource())
        .and()
            .exceptionHandling()
                .authenticationEntryPoint((ServerWebExchange exchange, AuthenticationException exception)->{
                    return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                })
                .accessDeniedHandler((ServerWebExchange exchange, AccessDeniedException exception)->{
                    return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                })
        .and()
            .authorizeExchange()
            .anyExchange()
            .authenticated()
        .and()
            .oauth2Login()
                .authenticationSuccessHandler((WebFilterExchange webFilterExchange, Authentication auth)->{
                    if(auth.getPrincipal() instanceof OAuth2User){
                        OAuth2User oAuth2User=(OAuth2User) auth.getPrincipal();
                        accountService.addOAuthUser(oAuth2User.getAttributes().get("email").toString());
                    }
                    ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();
                    ServerWebExchange exchange = webFilterExchange.getExchange();
                    return redirectStrategy.sendRedirect(exchange, URI.create(angularUrl));
                });
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(angularUrl));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowCredentials(true);
        //the below three lines will add the relevant CORS response headers
        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }}
