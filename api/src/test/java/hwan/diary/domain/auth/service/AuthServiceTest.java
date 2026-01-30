package hwan.diary.domain.auth.service;

import hwan.diary.common.exception.token.RefreshTokenMismatchException;
import hwan.diary.common.exception.token.RefreshTokenNotFoundException;
import hwan.diary.domain.auth.dto.request.OAuthUserRequest;
import hwan.diary.domain.auth.dto.response.AccessTokenResponse;
import hwan.diary.domain.auth.dto.response.TokenResponse;
import hwan.diary.domain.user.entity.SnsAccount;
import hwan.diary.domain.user.entity.User;
import hwan.diary.domain.user.repository.SnsAccountRepository;
import hwan.diary.domain.user.repository.UserRepository;
import hwan.diary.domain.user.service.UserService;
import hwan.diary.domain.user.values.Provider;
import hwan.diary.security.jwt.repository.RefreshTokenRepository;
import hwan.diary.security.jwt.token.JwtProvider;
import hwan.diary.security.jwt.token.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private SnsAccountRepository snsAccountRepository;
    @Mock private JwtProvider jwtProvider;
    @Mock private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthService authService;

    private OAuthUserRequest request;

    @BeforeEach
    void setUp() {
        request = new OAuthUserRequest(
            "user",
            Provider.GOOGLE,
            "google-1234",
            "example@google.com"
        );
    }

    @Test
    void loginOrSignUp_existingSnsAccount_returnsTokens_andDoesNotCreateUserOrSnsAccount() {
        // given
        User user = User.create("u", "test@example.com");
        setId(user, 10L);

        SnsAccount sns = SnsAccount.create(user, request.provider(), request.providerId(), request.email());

        given(snsAccountRepository.findByProviderAndProviderId(request.provider(), request.providerId()))
            .willReturn(Optional.of(sns));

        given(jwtProvider.generateToken(10L, TokenType.ACCESS)).willReturn("access");
        given(jwtProvider.generateToken(10L, TokenType.REFRESH)).willReturn("refresh");

        // when
        TokenResponse resp = authService.loginOrSignUp(request);

        // then
        assertEquals("Bearer", resp.tokenType());
        assertEquals("access", resp.accessToken());
        assertEquals("refresh", resp.refreshToken());

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(snsAccountRepository, never()).save(any(SnsAccount.class));
        verify(refreshTokenRepository).save(10L, "refresh");
    }

    @Test
    void loginOrSignUp_noSnsAccount_existingUserByEmail_createsSnsAccount_only() {
        // given
        given(snsAccountRepository.findByProviderAndProviderId(request.provider(), request.providerId()))
            .willReturn(Optional.empty());

        User existingUser = User.create("existing", request.email());
        setId(existingUser, 20L);

        given(userRepository.findByEmail(request.email()))
            .willReturn(Optional.of(existingUser));

        // snsAccountRepository.save 호출시 그냥 그대로 반환하도록
        given(snsAccountRepository.save(any(SnsAccount.class)))
            .willAnswer(inv -> inv.getArgument(0));

        given(jwtProvider.generateToken(20L, TokenType.ACCESS)).willReturn("access2");
        given(jwtProvider.generateToken(20L, TokenType.REFRESH)).willReturn("refresh2");

        // when
        TokenResponse resp = authService.loginOrSignUp(request);

        // then
        assertEquals("Bearer", resp.tokenType());
        assertEquals("access2", resp.accessToken());
        assertEquals("refresh2", resp.refreshToken());

        verify(userRepository).findByEmail(request.email());
        verify(userRepository, never()).save(any(User.class));
        verify(snsAccountRepository).save(any(SnsAccount.class));
        verify(refreshTokenRepository).save(20L, "refresh2");
    }

    @Test
    void loginOrSignUp_noSnsAccount_noUserByEmail_createsUserAndSnsAccount() {
        // given
        given(snsAccountRepository.findByProviderAndProviderId(request.provider(), request.providerId()))
            .willReturn(Optional.empty());

        given(userRepository.findByEmail(request.email()))
            .willReturn(Optional.empty());

        // save될 때 id가 생성되는 것처럼 흉내
        given(userRepository.save(any(User.class)))
            .willAnswer(inv -> {
                User u = inv.getArgument(0);
                setId(u, 30L);
                return u;
            });

        given(snsAccountRepository.save(any(SnsAccount.class)))
            .willAnswer(inv -> inv.getArgument(0));

        given(jwtProvider.generateToken(30L, TokenType.ACCESS)).willReturn("access3");
        given(jwtProvider.generateToken(30L, TokenType.REFRESH)).willReturn("refresh3");

        // when
        TokenResponse resp = authService.loginOrSignUp(request);

        // then
        assertEquals("Bearer", resp.tokenType());
        assertEquals("access3", resp.accessToken());
        assertEquals("refresh3", resp.refreshToken());

        verify(userRepository).findByEmail(request.email());
        verify(userRepository).save(any(User.class));
        verify(snsAccountRepository).save(any(SnsAccount.class));
        verify(refreshTokenRepository).save(30L, "refresh3");
    }

    /**
     * 엔티티에 setter가 없다면 테스트에서 id를 심어주기 위해 리플렉션 사용
     */
    private static void setId(Object entity, Long id) {
        try {
            var f = entity.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    void reissueAccessToken_whenSavedTokenValid_thenReturnsToken() {
        // given
        String refreshToken = "refresh_token";

        Claims claims = mock(Claims.class);
        given(jwtProvider.parseClaims("refresh_token", TokenType.REFRESH)).willReturn(claims);
        given(jwtProvider.getUserIdFromClaims(claims, TokenType.REFRESH)).willReturn(1L);
        given(refreshTokenRepository.get(1L)).willReturn("refresh_token");


        given(jwtProvider.generateToken(1L, TokenType.ACCESS)).willReturn("access_token");
        given(jwtProvider.generateToken(1L, TokenType.REFRESH)).willReturn("new_refresh_token");

        // when
        TokenResponse tokenResponse = authService.reissueAccessToken(refreshToken);

        // then
        assertEquals("Bearer", tokenResponse.tokenType());
        assertEquals("access_token", tokenResponse.accessToken());
        verify(refreshTokenRepository).save(1L, "new_refresh_token");
    }

    @Test
    void reissueAccessToken_whenSavedRefreshTokenNotFound_thenThrowRefreshTokenNotFoundException() {
        // given
        String refreshToken = "refresh_token";

        Claims claims = mock(Claims.class);
        given(jwtProvider.parseClaims("refresh_token", TokenType.REFRESH)).willReturn(claims);
        given(jwtProvider.getUserIdFromClaims(claims, TokenType.REFRESH)).willReturn(1L);
        given(refreshTokenRepository.get(1L)).willReturn(null);

        // when & then
        Assertions.assertThrows(RefreshTokenNotFoundException.class, () -> authService.reissueAccessToken(refreshToken));
    }

    @Test
    void reissueAccessToken_whenSavedRefreshTokenMismatch_thenThrowRefreshTokenMismatchException() {
        // given
        String refreshToken = "refresh_token";

        Claims claims = mock(Claims.class);
        given(jwtProvider.parseClaims("refresh_token", TokenType.REFRESH)).willReturn(claims);
        given(jwtProvider.getUserIdFromClaims(claims, TokenType.REFRESH)).willReturn(1L);
        given(refreshTokenRepository.get(1L)).willReturn("different_refresh_token");

        // when & then
        Assertions.assertThrows(RefreshTokenMismatchException.class, () -> authService.reissueAccessToken(refreshToken));
    }

}
