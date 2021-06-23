package io.github.oauth2.spring;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

public class SpringRedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {
    private final RedisTemplate<String, OAuth2Authentication> redisTemplate;

    public SpringRedisAuthorizationCodeServices(RedisTemplate<String, OAuth2Authentication> redisTemplate) {
        Assert.notNull(redisTemplate, "RedisTemplate required");
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        redisTemplate.opsForValue().set("oauth_code:" + code, authentication, 10, TimeUnit.MINUTES);
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        OAuth2Authentication token = redisTemplate.opsForValue().get("oauth_code:" + code);
        this.redisTemplate.delete("oauth_code:" + code);
        return token;
    }

}
