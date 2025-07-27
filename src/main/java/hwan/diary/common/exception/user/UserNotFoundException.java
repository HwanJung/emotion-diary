package hwan.diary.common.exception.user;

import hwan.diary.common.exception.ApplicationException;
import hwan.diary.common.exception.values.ErrorCode;
import lombok.Getter;

@Getter
public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
