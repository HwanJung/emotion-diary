package hwan.diary.security.jwt.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwan.diary.common.exception.token.TokenException;
import hwan.diary.common.exception.ErrorCode;
import hwan.diary.common.response.ErrorResponse;
import hwan.diary.security.jwt.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException
    ) throws IOException {
        Exception exception = (Exception) request.getAttribute("jwt_exception");

        ErrorResponse errorResponse;

        String uri = request.getRequestURI();
        String ipAddress = request.getRemoteAddr();

        if(exception instanceof TokenException tokenException) {
            ErrorCode errorCode = tokenException.getErrorCode();
            errorResponse = new ErrorResponse(errorCode);
        } else {

            errorResponse = new ErrorResponse(ErrorCode.UNAUTHORIZED);
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
