package hwan.diary.common.exception;

import lombok.Getter;
import org.slf4j.Logger;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;

    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public abstract void log(Logger log);
}
