package com.rentpal.gateway.service;/*
 * @author frank
 * @created 12 Dec,2020 - 5:34 PM
 */

import com.rentpal.gateway.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountService {

    private final WebClient.Builder webClientBuilder;

    private String accountService;

    @Autowired
    public AccountService(WebClient.Builder webClientBuilder){
        this.webClientBuilder=webClientBuilder;
    }

    @Value("${account.service}")
    public void setAccountService(String accountService){
        this.accountService=accountService;
    }

    public void addOAuthUser(String email){
        User user = new User();
        user.setEmail(email);
        WebClient webClient = webClientBuilder.baseUrl(this.accountService)
                .filter(logRequest()).build();
        webClient.post()
                .uri("/accounts/user")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(user), User.class)
                .retrieve()
                .bodyToMono(String.class);
    }

    // This method returns filter function which will log request data
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

}
