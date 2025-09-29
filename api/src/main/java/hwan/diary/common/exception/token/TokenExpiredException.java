package hwan.diary.common.exception.token;

import hwan.diary.common.exception.ErrorCode;
import hwan.diary.security.jwt.token.TokenType;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.time.Instant;

@Getter
public class TokenExpiredException extends TokenException{
    private final Long uid;
    private final Instant expiredAt;

    public TokenExpiredException(TokenType type, Long uid, Instant expiredAt) {
        super(type == TokenType.ACCESS ? ErrorCode.ACCESS_TOKEN_EXPIRED : ErrorCode.REFRESH_TOKEN_EXPIRED, type);
        this.uid = uid;
        this.expiredAt = expiredAt;
    }

    @Override
    public void log(Logger log) {
        log.warn("Access token expired. uid={}, exp={}", this.uid, this.expiredAt);
    }
}
