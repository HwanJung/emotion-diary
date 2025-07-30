package hwan.diary.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(buildKey(userId), refreshToken, 7, TimeUnit.DAYS);
    }

    public String get(Long userId) {
        return redisTemplate.opsForValue().get(buildKey(userId));
    }

    public void delete(Long userId) {
        redisTemplate.delete(buildKey(userId));
    }

    private String buildKey(Long userId) {
        return "refresh:" + userId;
    }
}
