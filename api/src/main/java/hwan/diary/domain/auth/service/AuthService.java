package hwan.diary.domain.auth.service;

import hwan.diary.domain.auth.dto.request.OAuthUserRequest;
import hwan.diary.domain.auth.dto.response.AccessTokenResponse;
import hwan.diary.domain.auth.dto.response.TokenResponse;
import hwan.diary.common.exception.token.RefreshTokenMismatchException;
import hwan.diary.common.exception.token.RefreshTokenNotFoundException;
import hwan.diary.domain.user.entity.SnsAccount;
import hwan.diary.domain.user.entity.User;
import hwan.diary.domain.user.repository.SnsAccountRepository;
import hwan.diary.domain.user.repository.UserRepository;
import hwan.diary.domain.user.service.UserService;
import hwan.diary.security.jwt.repository.RefreshTokenRepository;
import hwan.diary.security.jwt.token.JwtProvider;
import hwan.diary.security.jwt.token.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final SnsAccountRepository snsAccountRepository;

    /**
     * Handles user login request.
     * Generate an access token and a refresh token using user id.
     * Save a refresh token in redis.
     *
     * @param request OAuth login request containing provider info and user details
     * @return a generated access token and refresh token
     */
    @Transactional
    public TokenResponse loginOrSignUp(OAuthUserRequest request) {
        Long uid = findOrSaveUser(request);
        return getTokenResponse(uid);
    }

    private Long findOrSaveUser(OAuthUserRequest request) {
        SnsAccount existingSnsAccount = snsAccountRepository
            .findByProviderAndProviderId(request.provider(), request.providerId())
            .orElse(null);

        if (existingSnsAccount != null) {
            return existingSnsAccount.getUser().getId();
        }

        User user = userRepository.findByEmail(request.email())
            .orElseGet(() ->
                userRepository.save(
                    User.create(request.username(), request.email())
                )
            );


        SnsAccount snsAccount = SnsAccount.create(
            user,
            request.provider(),
            request.providerId(),
            request.email()
        );
        snsAccountRepository.save(snsAccount);

        return user.getId();
    }

    private TokenResponse getTokenResponse(Long uid) {
        String accessToken = jwtProvider.generateToken(uid, TokenType.ACCESS);
        String refreshToken = jwtProvider.generateToken(uid, TokenType.REFRESH);

        refreshTokenRepository.save(uid, refreshToken);

        return new TokenResponse("Bearer", accessToken, refreshToken);
    }

    /**
     * Logout logic. Delete a refresh token using user id.
     *
     * @param uid id of user to logout
     */
    @Transactional
    public void logout(Long uid) {
        refreshTokenRepository.delete(uid);
    }

    /**
     * Handles reissuing access token.
     * Check whether the refresh token exists in header, and generate a new access token.
     *
     * @param refreshToken containing refresh token in header.
     * @return a generated access token
     */
    @Transactional
    public TokenResponse reissueAccessToken(String refreshToken) {
        Claims claims = jwtProvider.parseClaims(refreshToken, TokenType.REFRESH);
        Long uid = jwtProvider.getUserIdFromClaims(claims, TokenType.REFRESH);

        String savedRefreshToken = refreshTokenRepository.get(uid);
        if(savedRefreshToken == null) {
            throw new RefreshTokenNotFoundException(uid);
        }
        if(!refreshToken.equals(savedRefreshToken)) {
            throw new RefreshTokenMismatchException(uid);
        }

        String accessToken = jwtProvider.generateToken(uid, TokenType.ACCESS);
        String newRefreshToken = jwtProvider.generateToken(uid, TokenType.REFRESH);
        refreshTokenRepository.save(uid, newRefreshToken);

        return new TokenResponse("Bearer", accessToken, refreshToken);

    }
}
