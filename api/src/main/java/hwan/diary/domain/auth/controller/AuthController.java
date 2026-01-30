package hwan.diary.domain.auth.controller;

import hwan.diary.domain.auth.dto.response.AccessTokenResponse;
import hwan.diary.domain.auth.dto.response.TokenResponse;
import hwan.diary.domain.auth.service.AuthService;
import hwan.diary.domain.auth.dto.request.OAuthUserRequest;
import hwan.diary.security.jwt.principal.JwtUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Receive OAuth user info and Respond with generated access token and refresh token.
     *
     * @param request the user info from OAuth
     * @return generated access token and refresh token
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody OAuthUserRequest request) {
        log.debug("[auth/login] attempt email={}", request.email());
        TokenResponse tokenResponse = authService.loginOrSignUp(request);
        log.info("[auth/login] success. email={}", request.email());
        return ResponseEntity.ok(tokenResponse);
    }

    /**
     * Authentication is needed.
     * logout logic
     *
     * @param principal the authenticated user info
     * @return no content response
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        log.debug("[auth/logout] attempt userId={}", userId);
        authService.logout(userId);
        log.info("[auth/logout] success. userId={}", userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Receive refresh token and respond with a generated access token.
     *
     * @param request containing refresh token
     * @return a generated access token
     */
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");
        TokenResponse accessTokenResponse = authService.reissueAccessToken(refreshToken);
        log.info("[auth/reissue] success. accessTokenResponse={}", accessTokenResponse);
        return ResponseEntity.ok(accessTokenResponse);
    }
}












