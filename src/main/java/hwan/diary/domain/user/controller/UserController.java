package hwan.diary.domain.user.controller;

import hwan.diary.domain.user.dto.request.UpdateProfileRequest;
import hwan.diary.domain.user.dto.response.UserResponse;
import hwan.diary.domain.user.service.UserService;
import hwan.diary.security.jwt.principal.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Return the user info.
     * The user must be authenticated by JWT token
     *
     * @param jwtUserPrincipal the authenticated user by JWT token
     * @return a user info as UserResponse.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyAccount(@AuthenticationPrincipal JwtUserPrincipal jwtUserPrincipal) {
        UserResponse userResponse = userService.findUserById(jwtUserPrincipal.id());
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Delete the user.
     * The user must be authenticated by JWT token
     *
     * @param jwtUserPrincipal the authenticated user by JWT token
     * @return HTTP 204 No content response
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal JwtUserPrincipal jwtUserPrincipal) {
        userService.deleteUser(jwtUserPrincipal.id());
        return ResponseEntity.noContent().build();
    }

    /**
     * Update the profile of the authenticated user.
     * The user must be authenticated by JWT token.
     *
     * @param jwtUserPrincipal the authenticated user by JWT token
     * @param request the request containing userName, profileImage to update
     * @return updated user info as UserResponse.
     */
    @PatchMapping("/me/profile")
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal JwtUserPrincipal jwtUserPrincipal,
            @RequestBody UpdateProfileRequest request) {
        UserResponse userResponse = userService.updateProfile(
                jwtUserPrincipal.id(), request);
        return ResponseEntity.ok(userResponse);
    }
}
