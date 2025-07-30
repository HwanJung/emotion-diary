package hwan.diary.common.exception.token;

import hwan.diary.common.exception.ErrorCode;
import hwan.diary.security.jwt.token.TokenType;
import lombok.Getter;

@Getter
public class TokenExpiredException extends TokenException{
    private final String expiredAt;

    public TokenExpiredException(TokenType type) {
        super(type == TokenType.ACCESS ? ErrorCode.ACCESS_TOKEN_EXPIRED : ErrorCode.REFRESH_TOKEN_EXPIRED, type);
    }
}
