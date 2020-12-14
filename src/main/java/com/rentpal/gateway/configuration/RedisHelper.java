package com.rentpal.gateway.configuration;

import com.rentpal.gateway.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * The type Redis helper.
 *
 * @author frank
 * @created 13 Dec,2020 - 6:41 PM A helper class for redis hash related operations on the UserDTO object.
 */
@Component
public class RedisHelper {

    private final ReactiveRedisOperations reactiveRedisTemplate;

    /**
     * Instantiates a new Redis helper object.
     *
     * @param reactiveRedisTemplate the reactive redis template
     */
    @Autowired
    public RedisHelper(ReactiveRedisOperations reactiveRedisTemplate){
        this.reactiveRedisTemplate=reactiveRedisTemplate;
    }

    /**
     * Adds a key value pair to a given hash.
     *
     * @param hash    the hash
     * @param key the hash key
     * @param val     the val
     */
    public void addToHash(String hash, String key, UserDTO val){
        reactiveRedisTemplate.opsForHash().put(hash, key, val).subscribe();
    }

    /**
     * Retrieves Mono - UserDTO for a given key and hash.
     *
     * @param hash    the hash
     * @param key the hash key
     * @return the mono
     */
    public Mono<UserDTO> getObjectFromHash(String hash, String key){
        return reactiveRedisTemplate.opsForHash().get(hash, key);
    }

    /**
     * Retrieves Mono - Boolean to check if value exists for a given key and hash.
     *
     * @param hash    the hash
     * @param key the hash key
     * @return the mono
     */
    public Mono<Boolean> isObjectPresentInHash(String hash, String key){
        return reactiveRedisTemplate.opsForHash().get(hash, key).hasElement();
    }
}
