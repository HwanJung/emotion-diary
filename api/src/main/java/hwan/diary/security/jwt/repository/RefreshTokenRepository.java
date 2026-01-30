package hwan.diary.security.jwt.repository;

import hwan.diary.security.jwt.token.TokenProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final TokenProperties tokenProperties;

    public void save(Long userId, String refreshToken) {
        stringRedisTemplate.opsForValue().set(
            String.valueOf(userId),
            refreshToken,
            tokenProperties.getRefreshTokenExpirationMs(),
            TimeUnit.MILLISECONDS
        );
    }

    public String get(Long userId) {
        return stringRedisTemplate.opsForValue().get(String.valueOf(userId));
    }

    public void delete(Long userId) {
        stringRedisTemplate.delete(String.valueOf(userId));
    }
}