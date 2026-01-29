package hwan.diary.security.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public void save(Long userId, String hashedToken) {
        stringRedisTemplate.opsForValue().set(hashedToken, String.valueOf(userId), 7, TimeUnit.DAYS);
    }

    public String get(String hashedToken) {
        return stringRedisTemplate.opsForValue().get(hashedToken);
    }

    public void delete(String hashedToken) {
        stringRedisTemplate.delete(hashedToken);
    }
}