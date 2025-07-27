package hwan.diary.common.exception;

import hwan.diary.common.exception.values.ErrorCode;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;

    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
