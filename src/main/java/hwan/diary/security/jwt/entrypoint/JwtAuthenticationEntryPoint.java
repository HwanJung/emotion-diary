package hwan.diary.security.jwt.entrypoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * When the token is invalid then JwtAuthException occur,
 * respond UNAUTHORIZED message.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException
    ) throws IOException {
        Throwable exception = (Throwable) request.getAttribute("jwt_exception");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
            {
                "error": "Unauthorized",
                "message": "%s"
            }
            """.formatted(msg != null ? msg : "Authentication required"));
    }
}
