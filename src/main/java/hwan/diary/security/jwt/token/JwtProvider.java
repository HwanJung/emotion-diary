package hwan.diary.security.jwt.token;

import hwan.diary.common.exception.token.TokenExpiredException;
import hwan.diary.common.exception.token.TokenInvalidException;
import hwan.diary.common.exception.token.TokenMissingException;
import hwan.diary.common.exception.token.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private static final long ACCESS_TOKEN_EXPIRATION_MS = 1000L * 60 * 60;
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 7;

    private final SecretKey secretKey;

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate a JWT token containing the user id as the subject.
     *
     * @param userId the user id to include in the token
     * @param type   the type of token (ACCESS or REFRESH)
     * @return generated JWT token as String
     */
    public String generateToken(Long userId, TokenType type) {
        String claimValue = type == TokenType.ACCESS ? "access" : "refresh";
        long expirationMs = type == TokenType.ACCESS ? ACCESS_TOKEN_EXPIRATION_MS : REFRESH_TOKEN_EXPIRATION_MS;

        return Jwts.builder()
            .subject(userId.toString())
            .claim("type", claimValue)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(secretKey)
            .compact();
    }

    /**
     * Validate the token and parse claims from the token.
     * Throws an exception, when the token is misses or expired or invalid.
     *
     * @param token the JWT token from the client's request
     * @param type  the token type(ACCESS or REFRESH)
     * @return Claims extracted from the token
     */
    public Claims parseClaims(String token, TokenType type) {
        if (token == null || token.trim().isEmpty()) {
            throw new TokenMissingException(type);
        }

        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            Claims claims = e.getClaims();
            Instant expirationTime = claims.getExpiration().toInstant();
            Long uid = getUserIdFromClaims(claims, type);

            throw new TokenExpiredException(type, uid, expirationTime);
        } catch (JwtException e) {
            throw new TokenInvalidException(type);
        }
    }

    /**
     * Extract user id from the claims.
     * Throws an exception, when subject is not Long type.
     *
     * @param claims extracted from a valid token.
     * @param type   the token type(ACCESS or REFRESH)
     * @return user id as Long
     */
    public Long getUserIdFromClaims(Claims claims, TokenType type) {
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new TokenInvalidException(type);
        }
    }
}

