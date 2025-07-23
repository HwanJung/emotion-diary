package hwan.diary.api.auth.service;

import hwan.diary.api.auth.dto.request.OAuthUserRequest;
import hwan.diary.domain.user.dto.response.UserResponse;
import hwan.diary.domain.user.service.UserService;
import hwan.diary.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    /**
     * Handles user login request
     * If the user does not exist, register them. Then generate and return a JWT access token
     *
     * @param request OAuth login request containing provider info and user details
     * @return generated JWT access token as a String
     */
    public String login(OAuthUserRequest request) {
        UserResponse userResponse = userService.findOrRegister(request);
        return jwtProvider.generateToken(userResponse.id());
    }
}
