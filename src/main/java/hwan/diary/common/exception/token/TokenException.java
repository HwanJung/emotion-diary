package hwan.diary.common.exception.token;

import hwan.diary.common.exception.ApplicationException;
import hwan.diary.common.exception.values.ErrorCode;
import lombok.Getter;

@Getter
public abstract class TokenException extends ApplicationException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
