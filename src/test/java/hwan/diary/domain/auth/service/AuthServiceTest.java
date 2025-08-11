package hwan.diary.domain.auth.service;

import hwan.diary.common.exception.token.RefreshTokenMismatchException;
import hwan.diary.common.exception.token.RefreshTokenNotFoundException;
import hwan.diary.domain.auth.dto.request.OAuthUserRequest;
import hwan.diary.domain.auth.dto.response.AccessTokenResponse;
import hwan.diary.domain.auth.dto.response.TokenResponse;
import hwan.diary.domain.user.service.UserService;
import hwan.diary.domain.user.values.Provider;
import hwan.diary.security.jwt.repository.RefreshTokenRepository;
import hwan.diary.security.jwt.token.JwtProvider;
import hwan.diary.security.jwt.token.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private UserService userService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_whenOAuthRequestValid_thenReturnsTokens() {
        // given
        OAuthUserRequest oAuthUserRequest = new OAuthUserRequest(
            "user",
            Provider.GOOGLE,
            "google-1234",
            "example@google.com"
        );

        given(userService.findOrRegister(oAuthUserRequest)).willReturn(1L);
        given(jwtProvider.generateToken(1L, TokenType.ACCESS)).willReturn("access_token");
        given(jwtProvider.generateToken(1L, TokenType.REFRESH)).willReturn("refresh_token");

        // when
        TokenResponse tokenResponse = authService.login(oAuthUserRequest);

        // then
        verify(refreshTokenRepository).save(1L, "refresh_token");

        Assertions.assertEquals("Bearer", tokenResponse.tokenType());
        Assertions.assertEquals("access_token", tokenResponse.accessToken());
        Assertions.assertEquals("refresh_token", tokenResponse.refreshToken());

    }

    @Test
    void logout_whenReceiveUid_thenDeleteRefreshToken() {
        // given
        Long uid = 1L;

        // when
        authService.logout(uid);

        // then
        verify(refreshTokenRepository).delete(1L);

    }

    @Test
    void reissueAccessToken_whenSavedTokenValid_thenReturnsAccessToken() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Refresh-Token")).willReturn("refresh_token");

        Claims claims = mock(Claims.class);
        given(jwtProvider.parseClaims("refresh_token", TokenType.REFRESH)).willReturn(claims);
        given(jwtProvider.getUserIdFromClaims(claims, TokenType.REFRESH)).willReturn(1L);
        given(refreshTokenRepository.get(1L)).willReturn("refresh_token");

        given(jwtProvider.generateToken(1L, TokenType.ACCESS)).willReturn("access_token");

        // when
        AccessTokenResponse accessTokenResponse = authService.reissueAccessToken(request);

        // then
        Assertions.assertEquals("Bearer", accessTokenResponse.tokenType());
        Assertions.assertEquals("access_token", accessTokenResponse.accessToken());
    }

    @Test
    void reissueAccessToken_whenSavedRefreshTokenNotFound_thenThrowRefreshTokenNotFoundException() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Refresh-Token")).willReturn("refresh_token");

        Claims claims = mock(Claims.class);
        given(jwtProvider.parseClaims("refresh_token", TokenType.REFRESH)).willReturn(claims);
        given(jwtProvider.getUserIdFromClaims(claims, TokenType.REFRESH)).willReturn(1L);
        given(refreshTokenRepository.get(1L)).willReturn(null);

        // when & then
        Assertions.assertThrows(RefreshTokenNotFoundException.class, () -> authService.reissueAccessToken(request));
    }

    @Test
    void reissueAccessToken_whenSavedRefreshTokenMismatch_thenThrowRefreshTokenMismatchException() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Refresh-Token")).willReturn("refresh_token");

        Claims claims = mock(Claims.class);
        given(jwtProvider.parseClaims("refresh_token", TokenType.REFRESH)).willReturn(claims);
        given(jwtProvider.getUserIdFromClaims(claims, TokenType.REFRESH)).willReturn(1L);
        given(refreshTokenRepository.get(1L)).willReturn("different_refresh_token");

        // when & then
        Assertions.assertThrows(RefreshTokenMismatchException.class, () -> authService.reissueAccessToken(request));
    }

}
