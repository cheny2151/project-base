package com.cheney.javaconfig.redis;

import com.cheney.redis.client.RedisClient;
import com.cheney.redis.client.impl.JsonRedisClient;
import com.cheney.redis.clustertask.sub.ClusterTaskRedisSub;
import com.cheney.redis.lock.LockConstant;
import com.cheney.redis.lock.awaken.listener.SpringSubLockManager;
import com.cheney.redis.proxy.RedisLogProxy;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.cheney.redis.clustertask.pub.ClusterTaskPublisher.CLUSTER_TASK_CHANNEL_PRE_KEY;

/**
 * redis配置
 */
@Configuration
@AutoConfigureAfter(value = RedisAutoConfiguration.class)
public class RedisConfig {

    @Bean("redisTemplate")
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean("jsonRedisTemplate")
    public RedisTemplate jsonRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean("jdkRedisTemplate")
    public RedisTemplate jdkRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        Map<MessageListener, Collection<? extends Topic>> messageListeners = new HashMap<>();
        messageListeners.put(subLockManager(), Collections.singleton(new PatternTopic(LockConstant.LOCK_CHANNEL + "*")));
        messageListeners.put(clusterTaskRedisSub(), Collections.singleton(new PatternTopic(CLUSTER_TASK_CHANNEL_PRE_KEY + "*")));
        container.setMessageListeners(messageListeners);
        return container;
    }

    @Bean("springSubLockManager")
    public SpringSubLockManager subLockManager() {
        return new SpringSubLockManager();
    }

    @Bean("clusterTaskSub")
    public ClusterTaskRedisSub clusterTaskRedisSub() {
        return new ClusterTaskRedisSub();
    }

    /**
     * jdk proxy测试类
     */
    @Bean
    public RedisClient redisClientWithLog(JsonRedisClient redisClient) {
        return RedisLogProxy.newProxyInstance(redisClient);
    }
}
