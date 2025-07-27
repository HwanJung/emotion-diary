package hwan.diary.common.exception.token;

import hwan.diary.common.exception.values.ErrorCode;
import hwan.diary.domain.auth.token.TokenType;

public class TokenExpiredException extends TokenException{
    public TokenExpiredException(TokenType type) {
        super(type == TokenType.ACCESS ? ErrorCode.ACCESS_TOKEN_EXPIRED : ErrorCode.REFRESH_TOKEN_EXPIRED);
    }
}
