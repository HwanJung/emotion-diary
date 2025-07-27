package hwan.diary.common.exception.token;

import hwan.diary.common.exception.ApplicationException;
import hwan.diary.common.exception.values.ErrorCode;

public class RefreshTokenNotFoundException extends ApplicationException {
    public RefreshTokenNotFoundException() {
        super(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }
}
