package hwan.diary.common.exception.token;

import hwan.diary.common.exception.ApplicationException;
import hwan.diary.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class RefreshTokenMismatchException extends ApplicationException {

    public RefreshTokenMismatchException() {
        super(ErrorCode.REFRESH_TOKEN_MISMATCH);
    }

}
