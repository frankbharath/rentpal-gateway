package com.rentpal.gateway.filter;

import com.rentpal.gateway.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * @author frank
 * @created 13 Dec,2020 - 1:56 AM
 *
 * A pre global filter that intercepts all the requests and adds user information for downstream services.
 */

@Component
public class PreGlobalFilter implements GlobalFilter {

    private final AccountService accountService;

    /**
     * Instantiates a new Pre global filter.
     *
     * @param accountService the account service
     */
    @Autowired
    public PreGlobalFilter(AccountService accountService){
        this.accountService=accountService;
    }

    /**
     * Intercepts the request and adds user email and id for the downstream service
     *
     * @param accountService the account service
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .map(authentication -> authentication)
            .flatMap(accountService::getUser).map(userDTO -> {
                exchange.getRequest().mutate()
                    .header("email", userDTO.getEmail())
                    .header("id", userDTO.getId().toString()).build();
                return exchange;
            }).flatMap(chain::filter);
    }
}
