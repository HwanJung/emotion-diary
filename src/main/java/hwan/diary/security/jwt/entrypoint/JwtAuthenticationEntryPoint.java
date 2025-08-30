package hwan.diary.security.jwt.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwan.diary.common.exception.token.TokenException;
import hwan.diary.common.exception.ErrorCode;
import hwan.diary.common.exception.token.TokenExpiredException;
import hwan.diary.common.exception.token.TokenInvalidException;
import hwan.diary.common.exception.token.TokenMissingException;
import hwan.diary.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * If the token is invalid, a TokenException is thrown.
 * Respond with an ErrorResponse that reflects the corresponding errorCode.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException
    ) throws IOException {
        Exception exception = (Exception) request.getAttribute("jwt_exception");

        ErrorResponse errorResponse;

        if(exception instanceof TokenException tokenException) {
            ErrorCode errorCode = tokenException.getErrorCode();
            errorResponse = new ErrorResponse(errorCode);

            tokenException.log(log);
        } else {
            errorResponse = new ErrorResponse(ErrorCode.UNAUTHORIZED);

            log.warn("Unauthorized access token");
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
