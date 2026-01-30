package hwan.diary.security.jwt.token;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@Getter
public class TokenProperties {

    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public TokenProperties(@Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
                           @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs) {
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }
}
