package com.rentpal.gateway.service;

import com.rentpal.gateway.configuration.RedisHelper;
import com.rentpal.gateway.dto.UserDTO;
import com.rentpal.gateway.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author frank
 * @created 12 Dec,2020 - 5:34 PM
 *
 * This class helps us to perform user related operations after successful login.
 */
@Service
@Slf4j
public class AccountService {

    private final WebClient webClient;

    private final RedisHelper redisHelper;

    public static final String USER_HASH="users";

    private static final String USER_API="/user";

    /**
     * Instantiates a new Account service.
     *
     * @param webClient   the web client
     * @param redisHelper the redis helper
     */
    @Autowired
    public AccountService(WebClient webClient, RedisHelper redisHelper){
        this.webClient=webClient;
        this.redisHelper=redisHelper;
    }

    /**
     * Adds gmail and facebook users information to accounts service after successful login.
     *
     * @param auth the auth
     */
    public void addOAuthUser(Authentication auth){
        OAuth2User oAuth2User=(OAuth2User) auth.getPrincipal();
        String email=oAuth2User.getAttributes().get("email").toString();
        // checks the cache if user is present, if not makes an api call to account service to store the information
        redisHelper.isObjectPresentInHash(USER_HASH, email)
            .subscribe(value -> {
                    if(!value){
                        User user = new User();
                        user.setEmail(email);
                        webClient
                            .post()
                            .uri(USER_API)
                            .accept(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(Mono.just(user), User.class)
                            .retrieve()
                            .bodyToMono(UserDTO.class).subscribe(userDTO -> {
                                // save the information to redis cache
                                redisHelper.addToHash(USER_HASH, email, userDTO);
                                log.info("Added user info to the database");
                            });
                    }
                }
            );
    }

    /**
     * Gets user information from cache or from accounts service.
     *
     * @param auth the auth
     * @return the mono
     */
    public Mono<UserDTO> getUser(Authentication auth){
        OAuth2User oAuth2User=(OAuth2User) auth.getPrincipal();
        String email=oAuth2User.getAttributes().get("email").toString();
        return redisHelper.isObjectPresentInHash(USER_HASH, email)
                .flatMap(value -> {
                    if(value){
                        return redisHelper.getObjectFromHash(USER_HASH, email).map(userDTO -> userDTO);
                    }else{
                        return webClient
                                .get()
                                .uri(uriBuilder -> uriBuilder.path(USER_API).queryParam("email", email).build())
                                .header("email", email)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(UserDTO.class).map(userDTO -> {
                                    // save the information to redis cache
                                    redisHelper.addToHash(USER_HASH, email, userDTO);
                                    return userDTO;
                                });
                    }
                });
    }

}
