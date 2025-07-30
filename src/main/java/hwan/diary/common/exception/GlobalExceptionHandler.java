package hwan.diary.common.exception;

import hwan.diary.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions thrown from the application domain.
     * Logging and respond with error code.
     *
     * @param exception the application exception
     * @return ResponseEntity containing error information.
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException exception) {
        exception.log(log);

        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode);

        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

}
