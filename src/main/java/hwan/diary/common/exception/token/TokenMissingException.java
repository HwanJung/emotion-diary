package hwan.diary.common.exception.token;

import hwan.diary.common.exception.values.ErrorCode;
import hwan.diary.domain.auth.token.TokenType;

public class TokenMissingException extends TokenException {
    public TokenMissingException(TokenType type) {
        super(type == TokenType.ACCESS ? ErrorCode.ACCESS_TOKEN_MISSING : ErrorCode.REFRESH_TOKEN_MISSING);
    }
}
