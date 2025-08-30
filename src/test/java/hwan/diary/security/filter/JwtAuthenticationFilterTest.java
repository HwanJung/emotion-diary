package hwan.diary.security.filter;

import hwan.diary.common.exception.token.TokenExpiredException;
import hwan.diary.common.exception.token.TokenInvalidException;
import hwan.diary.common.exception.token.TokenMissingException;
import hwan.diary.security.jwt.filter.JwtAuthenticationFilter;
import hwan.diary.security.jwt.principal.JwtAuthenticationToken;
import hwan.diary.security.jwt.token.JwtProvider;
import hwan.diary.security.jwt.token.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private AuthenticationEntryPoint authenticationEntryPoint;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSkipFilter_forWhitelistedLoginPath() throws Exception {
        request = new MockHttpServletRequest("POST", "/api/auth/login");
        response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(jwtProvider, never()).parseClaims(any(), any());
        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
        verify(filterChain, times(1)).doFilter(any(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_whenValidToken_thenSetAuthentication() throws Exception {
        // given
        String token = "validToken";
        request.addHeader("Authorization", "Bearer " + token);

        Claims claims = mock(Claims.class);
        given(jwtProvider.parseClaims(token, TokenType.ACCESS)).willReturn(claims);
        given(jwtProvider.getUserIdFromClaims(claims, TokenType.ACCESS)).willReturn(1L);

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("1", auth.getName());
        assertInstanceOf(JwtAuthenticationToken.class, auth);
    }

    @Test
    void doFilterInternal_whenTokenMissing_thenThrowException() throws Exception {
        // given:
        given(jwtProvider.parseClaims(null, TokenType.ACCESS)).willThrow(new TokenMissingException(TokenType.ACCESS));

        // when & then
        assertThrows(InsufficientAuthenticationException.class, () ->
            jwtAuthenticationFilter.doFilter(request, response, filterChain)
        );
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertInstanceOf(TokenMissingException.class, request.getAttribute("jwt_exception"));

    }

    @Test
    void doFilterInternal_whenExpiredToken_thenThrowException() throws Exception {
        // given
        String expiredToken = "expiredToken";
        request.addHeader("Authorization", "Bearer " + expiredToken);

        given(jwtProvider.parseClaims(expiredToken, TokenType.ACCESS)).willThrow(new TokenExpiredException(TokenType.ACCESS, 1L, Instant.now()));

        // when & then
        assertThrows(InsufficientAuthenticationException.class, () ->
            jwtAuthenticationFilter.doFilter(request, response, filterChain)
        );
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertInstanceOf(TokenExpiredException.class, request.getAttribute("jwt_exception"));
    }

    @Test
    void doFilterInternal_whenInvalidToken_thenThrowException() throws Exception {
        // given
        String invalidToken = "invalidToken";
        request.addHeader("Authorization", "Bearer " + invalidToken);

        given(jwtProvider.parseClaims(invalidToken, TokenType.ACCESS)).willThrow(new TokenInvalidException(TokenType.ACCESS));

        // when & then
        assertThrows(InsufficientAuthenticationException.class, () ->
            jwtAuthenticationFilter.doFilter(request, response, filterChain)
        );
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertInstanceOf(TokenInvalidException.class, request.getAttribute("jwt_exception"));
    }
}
