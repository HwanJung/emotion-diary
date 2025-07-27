package hwan.diary.common.exception;

import hwan.diary.common.exception.token.TokenException;
import hwan.diary.common.exception.user.UserNotFoundException;
import hwan.diary.common.exception.values.ErrorCode;
import hwan.diary.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    /**
//     * When a user to find not exist in DB, send User Not Found error response.
//     *
//     * @param exception user not found exception (invalid uid)
//     * @return HTTP NOT FOUND 404 response
//     */
//    @ExceptionHandler({UserNotFoundException.class})
//    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception) {
//        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode());
//
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
//    }
//
//    /**
//     * When access token or refresh token is invalid, Send Authentication Error response.
//     *
//     * @param exception token exception (expired, missing, invalid)
//     * @return HTTP UNAUTHORIZED 401 response
//     */
//    @ExceptionHandler({TokenException.class})
//    public ResponseEntity<ErrorResponse> handleTokenException(TokenException exception) {
//        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode());
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
//    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ErrorResponse errorResponse = new ErrorResponse(errorCode);

        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

}
