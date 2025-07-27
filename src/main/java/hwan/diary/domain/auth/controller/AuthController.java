package hwan.diary.domain.auth.controller;

import hwan.diary.domain.auth.dto.response.AccessTokenResponse;
import hwan.diary.domain.auth.dto.response.TokenResponse;
import hwan.diary.domain.auth.service.AuthService;
import hwan.diary.domain.auth.dto.request.OAuthUserRequest;
import hwan.diary.security.jwt.principal.JwtUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    /**
     * Authentication is needed.
     * logout logic
     *
     * @param userPrincipal the authenticated user info
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal JwtUserPrincipal userPrincipal) {
        authService.logout(userPrincipal.id());
        return ResponseEntity.noContent().build();
    }

    /**
     * Receive refresh token and respond with a generated access token.
     *
     * @param request containing refresh token
     * @return a generated access token
     */
    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissue(@RequestBody HttpServletRequest request) {
        AccessTokenResponse accessTokenResponse = authService.reissueAccessToken(request);
        return ResponseEntity.ok(accessTokenResponse);
    }
}
