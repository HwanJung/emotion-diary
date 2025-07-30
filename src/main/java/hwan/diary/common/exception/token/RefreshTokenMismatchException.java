package hwan.diary.common.exception.token;

import hwan.diary.common.exception.ApplicationException;
import hwan.diary.common.exception.ErrorCode;
import lombok.Getter;
import org.slf4j.Logger;

@Getter
public class RefreshTokenMismatchException extends ApplicationException {

    private final Long uid;

    public RefreshTokenMismatchException(Long uid) {
        super(ErrorCode.REFRESH_TOKEN_MISMATCH);
        this.uid = uid;
    }

    @Override
    public void log(Logger log) {
        log.warn("Provided refresh token does not match stored token. uid={}", uid);
    }
}
