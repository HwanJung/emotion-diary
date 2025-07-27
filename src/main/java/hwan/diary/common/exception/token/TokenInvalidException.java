package hwan.diary.common.exception.token;

import hwan.diary.common.exception.values.ErrorCode;
import hwan.diary.domain.auth.token.TokenType;

public class TokenInvalidException extends TokenException{
    public TokenInvalidException(TokenType type) {
        super(type == TokenType.ACCESS ? ErrorCode.ACCESS_TOKEN_INVALID : ErrorCode.REFRESH_TOKEN_INVALID);
    }
}
