package hwan.diary.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.web.ErrorResponse;

public class DefaultErrorResponse implements ErrorResponse {
    private final HttpStatusCode statusCode;
    private final ProblemDetail body;

    public DefaultErrorResponse(HttpStatus status, String message) {
        this.statusCode = status;

        this.body = ProblemDetail.forStatusAndDetail(status, message);
        this.body.setTitle("Error");
    }

    @Override
    @NonNull
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    @NonNull
    public ProblemDetail getBody() {
        return body;
    }
}
