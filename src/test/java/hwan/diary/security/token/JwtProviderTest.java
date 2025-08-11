package hwan.diary.security.token;

import hwan.diary.common.exception.token.TokenExpiredException;
import hwan.diary.common.exception.token.TokenInvalidException;
import hwan.diary.common.exception.token.TokenMissingException;
import hwan.diary.security.jwt.token.JwtProvider;
import hwan.diary.security.jwt.token.TokenType;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class JwtProviderTest {

    private final JwtProvider jwtProvider = new JwtProvider("12345678901234567890123456789012", 3600000, 36000000);

    @Test
    void parseClaims_whenMissingToken_thenThrowException() {
        assertThrows(TokenMissingException.class, () ->
            jwtProvider.parseClaims(null, TokenType.ACCESS)
        );
    }

    @Test
    void parseClaims_whenExpiredToken_thenThrowException() throws InterruptedException {
        // given
        JwtProvider shortLivedProvider = new JwtProvider("12345678901234567890123456789012", 1, 1);

        String accessToken = shortLivedProvider.generateToken(1L, TokenType.ACCESS);
        String refreshToken = shortLivedProvider.generateToken(1L, TokenType.REFRESH);
        Thread.sleep(50);

        // when & then
        assertThrows(TokenExpiredException.class, () ->
            shortLivedProvider.parseClaims(accessToken, TokenType.ACCESS)
        );
        assertThrows(TokenExpiredException.class, () ->
            shortLivedProvider.parseClaims(refreshToken, TokenType.REFRESH)
        );
    }

    @Test
    void parseClaims_whenInvalidToken_thenThrowException() {
        assertThrows(TokenInvalidException.class, () ->
            jwtProvider.parseClaims("invalid.token.example", TokenType.ACCESS)
        );
    }
}
