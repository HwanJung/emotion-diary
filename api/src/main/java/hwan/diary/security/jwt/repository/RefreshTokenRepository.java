package hwan.diary.security.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public void save(Long userId, String refreshToken) {
        stringRedisTemplate.opsForValue().set(buildKey(userId), refreshToken, 7, TimeUnit.DAYS);
    }

    public String get(Long userId) {
        return stringRedisTemplate.opsForValue().get(buildKey(userId));
    }

    public void delete(Long userId) {
        stringRedisTemplate.delete(buildKey(userId));
    }

    private String buildKey(Long userId) {
        return "refresh:" + userId;
    }
}