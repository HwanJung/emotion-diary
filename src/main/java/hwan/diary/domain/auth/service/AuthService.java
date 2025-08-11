package hwan.diary.domain.auth.service;

import hwan.diary.domain.auth.dto.request.OAuthUserRequest;
import hwan.diary.domain.auth.dto.response.AccessTokenResponse;
import hwan.diary.domain.auth.dto.response.TokenResponse;
import hwan.diary.common.exception.token.RefreshTokenMismatchException;
import hwan.diary.common.exception.token.RefreshTokenNotFoundException;
import hwan.diary.domain.user.service.UserService;
import hwan.diary.security.jwt.repository.RefreshTokenRepository;
import hwan.diary.security.jwt.token.JwtProvider;
import hwan.diary.security.jwt.token.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Handles user login request.
     * Generate an access token and a refresh token using user id.
     * Save a refresh token in redis.
     *
     * @param request OAuth login request containing provider info and user details
     * @return a generated access token and refresh token
     */
    public TokenResponse login(OAuthUserRequest request) {
        Long uid = userService.findOrRegister(request);

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
    public void logout(Long uid) {
        refreshTokenRepository.delete(uid);
    }

    /**
     * Handles reissue request.
     * Check whether the refresh token exists in header, and generate a new access token.
     *
     * @param request containing refresh token in header.
     * @return a generated access token
     */
    public AccessTokenResponse reissueAccessToken(HttpServletRequest request) {

        String refreshToken = request.getHeader("Refresh-Token");

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

        return new AccessTokenResponse("Bearer", accessToken);

    }
}
