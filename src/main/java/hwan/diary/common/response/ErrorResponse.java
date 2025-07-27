package hwan.diary.common.response;

import hwan.diary.common.exception.values.ErrorCode;

public record ErrorResponse(
    String code,
    String message
) {
    public ErrorResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }
}
