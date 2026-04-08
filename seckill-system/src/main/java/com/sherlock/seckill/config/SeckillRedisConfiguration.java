package com.sherlock.seckill.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.List;

/**
 * 手动装配 Redis：仅在 seckill.redis.enabled=true 时创建连接，避免未启动 Redis 时自动配置反复重试。
 */
@Configuration
@ConditionalOnProperty(prefix = "seckill.redis", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(RedisProperties.class)
public class SeckillRedisConfiguration {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties props) {
        RedisStandaloneConfiguration standalone = new RedisStandaloneConfiguration();
        standalone.setHostName(props.getHost());
        standalone.setPort(props.getPort());
        if (props.getPassword() != null && !props.getPassword().isEmpty()) {
            standalone.setPassword(props.getPassword());
        }
        standalone.setDatabase(props.getDatabase());
        return new LettuceConnectionFactory(standalone);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        return template;
    }

    /**
     * 原子扣减库存：返回值 1=成功，0=库存不足，-1=key 不存在
     */
    @Bean
    public RedisScript<Long> stockDecrScript() {
        String lua =
                "local k = KEYS[1]\n"
                        + "local v = redis.call('GET', k)\n"
                        + "if v == false then return -1 end\n"
                        + "v = tonumber(v)\n"
                        + "if v < 1 then return 0 end\n"
                        + "redis.call('DECR', k)\n"
                        + "return 1";
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(lua);
        script.setResultType(Long.class);
        return script;
    }

    public static List<String> singleKey(String key) {
        return Collections.singletonList(key);
    }
}
