package hwan.diary.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Information of the occurred error.
 * Error Response is created using this Enum
 */
@Getter
public enum ErrorCode {

    // When handle TokenException
    ACCESS_TOKEN_EXPIRED("ACCESS_TOKEN_EXPIRED", "Access token expired.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED", "Refresh token expired.", HttpStatus.UNAUTHORIZED),

    ACCESS_TOKEN_INVALID("ACCESS_TOKEN_INVALID", "Access token is invalid", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("REFRESH_TOKEN_INVALID", "Refresh token is invalid", HttpStatus.UNAUTHORIZED),

    ACCESS_TOKEN_MISSING("ACCESS_TOKEN_MISSING", "Access token is missing", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_MISSING("REFRESH_TOKEN_MISSING", "Refresh token is missing", HttpStatus.UNAUTHORIZED),

    // Default UNAUTHORIZED code
    UNAUTHORIZED("UNAUTHORIZED", "Authorized required", HttpStatus.UNAUTHORIZED),

    // When handle RefreshTokenMismatch Exception
    REFRESH_TOKEN_MISMATCH("REFRESH_TOKEN_MISMATCH", "Refresh token mismatch", HttpStatus.UNAUTHORIZED),

    // When handle RefreshTokenNotFoundException
    REFRESH_TOKEN_NOT_FOUND("REFRESH_TOKEN_NOT_FOUND", "Refresh token not found", HttpStatus.UNAUTHORIZED),

    // When handle UserNotFoundException
    USER_NOT_FOUND("USER_NOT_FOUND", "Requested user is not exist", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
