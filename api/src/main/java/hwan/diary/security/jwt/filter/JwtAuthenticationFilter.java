package hwan.diary.security.jwt.filter;

import hwan.diary.common.exception.token.TokenException;
import hwan.diary.security.jwt.principal.JwtAuthenticationToken;
import hwan.diary.security.jwt.token.JwtProvider;
import hwan.diary.security.jwt.principal.JwtUserPrincipal;
import hwan.diary.security.jwt.token.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT authentication filter.
 * Resolve the JWT token from client request and validate the token.
 * Register authentication info to SecurityContext.
 * <p>
 * Throws an exception, when JWT token is expired or invalid.
 */

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AuthenticationEntryPoint entryPoint;

    private static final PathPatternRequestMatcher.Builder P =
        PathPatternRequestMatcher.withDefaults();

    private static final RequestMatcher WHITELIST = new OrRequestMatcher(
        P.matcher(HttpMethod.POST, "/api/auth/login"),
        P.matcher(HttpMethod.POST, "/api/auth/reissue")
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return WHITELIST.matches(request);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        try {
            String token = resolveToken(request);

            Claims claims = jwtProvider.parseClaims(token, TokenType.ACCESS);
            Long userId = jwtProvider.getUserIdFromClaims(claims, TokenType.ACCESS);

            JwtUserPrincipal principal = new JwtUserPrincipal(userId);
            Authentication authentication = new JwtAuthenticationToken(principal);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (TokenException e) {
            SecurityContextHolder.clearContext();

            request.setAttribute("jwt_exception", e);

            entryPoint.commence(request, response, new InsufficientAuthenticationException(e.getMessage(), e));
        } catch (Exception e) {
            SecurityContextHolder.clearContext();

            throw e;
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);  // remove "Bearer "
        }
        return null;
    }
}
