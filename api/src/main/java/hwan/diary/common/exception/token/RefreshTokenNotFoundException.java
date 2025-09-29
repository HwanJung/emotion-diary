package hwan.diary.common.exception.token;

import hwan.diary.common.exception.ApplicationException;
import hwan.diary.common.exception.ErrorCode;
import lombok.Getter;
import org.slf4j.Logger;

public class RefreshTokenNotFoundException extends ApplicationException {

    private final Long uid;

    public RefreshTokenNotFoundException(Long uid) {
        super(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        this.uid = uid;
    }

    @Override
    public void log(Logger log) {
        log.warn("Refresh token not found in server. uid={}", uid);
    }
}
