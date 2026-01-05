package com.tyreplus.dealer.infrastructure.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tyreplus.dealer.domain.entity.Lead;
import com.tyreplus.dealer.domain.entity.RechargePackage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Lead> leadRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Lead> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        JavaType type = mapper.getTypeFactory()
                .constructType(Lead.class);

        Jackson2JsonRedisSerializer<Lead> serializer =
                new Jackson2JsonRedisSerializer<>(type);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }


    @Bean
    public RedisTemplate<String, RechargePackage> rechargePackageRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, RechargePackage> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        JavaType type = mapper.getTypeFactory()
                .constructType(RechargePackage.class);

        Jackson2JsonRedisSerializer<RechargePackage> serializer =
                new Jackson2JsonRedisSerializer<>(type);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, List<RechargePackage>> rechargePackageListRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, List<RechargePackage>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        JavaType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, RechargePackage.class);

        Jackson2JsonRedisSerializer<List<RechargePackage>> serializer =
                new Jackson2JsonRedisSerializer<>(listType);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }


}

