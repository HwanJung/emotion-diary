package hwan.diary.common.exception.user;

import hwan.diary.common.exception.ApplicationException;
import hwan.diary.common.exception.ErrorCode;
import lombok.Getter;
import org.slf4j.Logger;

@Getter
public class UserNotFoundException extends ApplicationException {

    private final Long uid;

    public UserNotFoundException(ErrorCode errorCode, Long uid) {
        super(errorCode);
        this.uid = uid;
    }

    @Override
    public void log(Logger log) {
        log.warn("User not found in DB. uid={}", uid);
    }
}
