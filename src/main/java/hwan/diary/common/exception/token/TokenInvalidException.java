package hwan.diary.common.exception.token;

import hwan.diary.common.exception.ErrorCode;
import hwan.diary.security.jwt.token.TokenType;
import lombok.Getter;

@Getter
public class TokenInvalidException extends TokenException{
    private final TokenType tokenType;

    public TokenInvalidException(TokenType type) {
        super(type == TokenType.ACCESS ? ErrorCode.ACCESS_TOKEN_INVALID : ErrorCode.REFRESH_TOKEN_INVALID);
        this.tokenType = type;
    }

}
