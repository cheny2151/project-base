package com.cheney.javaconfig.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis配置
 */
@Configuration
public class RedisConfig {

    private final Environment env;

    @Autowired
    public RedisConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public JedisPoolConfig fitJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(env.getRequiredProperty("redis.maxIdle", int.class));
        jedisPoolConfig.setMaxTotal(env.getRequiredProperty("redis.maxActive", int.class));
        jedisPoolConfig.setMaxWaitMillis(env.getRequiredProperty("redis.maxWait", int.class));
        jedisPoolConfig.setTestOnBorrow(env.getRequiredProperty("redis.testOnBorrow", boolean.class));
        return jedisPoolConfig;
    }

    @Bean
    public JedisConnectionFactory fitJedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory(fitJedisPoolConfig());
        factory.setUsePool(true);
        factory.setHostName(env.getRequiredProperty("redis.host"));
        factory.setPort(env.getRequiredProperty("redis.port", int.class));
        factory.setTimeout(env.getRequiredProperty("redis.timeout", int.class));
        if (env.getRequiredProperty("redis.auth", boolean.class)) {
            factory.setPassword(env.getProperty("redis.password"));
        }
        return factory;
    }

    @Bean("stringRedisSerializerTemplate")
    public RedisTemplate fitStringTemplate() {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(fitJedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean("jsonRedisSerializerTemplate")
    public RedisTemplate fitJsonRedisTemplate() {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(fitJedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean("jdkRedisSerializerTemplate")
    public RedisTemplate fitJdkRedisSerializerTemplate() {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(fitJedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        return template;
    }

}
