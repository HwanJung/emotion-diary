package hwan.diary.common.exception.token;

import hwan.diary.common.exception.ApplicationException;
import hwan.diary.common.exception.ErrorCode;
import hwan.diary.security.jwt.token.TokenType;
import lombok.Getter;
import org.slf4j.Logger;

@Getter
public abstract class TokenException extends ApplicationException {
    private final TokenType tokenType;

    public TokenException(ErrorCode errorCode, TokenType tokenType) {
        super(errorCode);
        this.tokenType = tokenType;
    }

    public abstract void log(Logger log);

}
