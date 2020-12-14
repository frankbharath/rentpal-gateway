package com.rentpal.gateway.configuration;

import com.rentpal.gateway.dto.UserDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

/**
 * @author frank
 * @created 12 Dec,2020 - 10:47 PM
 * This class contains redis related configurations.
 */

@Configuration
// To store user session in the redis instead of JVM memory, ensures scalability.
@EnableRedisWebSession
public class SessionConfiguration {


    /**
     * Creates a redis template that can used to store data in redis and this is a blocking I/O operation
     * but still works in reactive environment.
     *
     * @return the redis template
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(new LettuceConnectionFactory());
        return template;
    }

    /**
     * Creates a non blocking I/O redis operation for UserDTO object.
     *
     * @param factory the factory
     * @return the reactive redis operations
     */
    @Bean
    public ReactiveRedisOperations<String, UserDTO> redisOperationForUserDTO(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<UserDTO> valueSerializer = new Jackson2JsonRedisSerializer<>(UserDTO.class);
        StringRedisSerializer stringRedisSerializer=new StringRedisSerializer();

        RedisSerializationContext.RedisSerializationContextBuilder<String, UserDTO> builder 
                = RedisSerializationContext.newSerializationContext(stringRedisSerializer);
        builder.hashKey(stringRedisSerializer);
        builder.hashValue(valueSerializer);

        RedisSerializationContext<String, UserDTO> context = builder.build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
