package hwan.diary.api.auth.controller;

import hwan.diary.api.auth.dto.response.JwtResponse;
import hwan.diary.api.auth.service.AuthService;
import hwan.diary.api.auth.dto.request.OAuthUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
     * Receive OAuth user info and Respond with generated JWT token.
     *
     * @param request the user info from OAuth
     * @return generated JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody OAuthUserRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
