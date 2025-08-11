package hwan.diary.domain.user.controller;

import hwan.diary.domain.user.dto.request.UpdateProfileRequest;
import hwan.diary.domain.user.dto.response.UserResponse;
import hwan.diary.domain.user.service.UserService;
import hwan.diary.security.jwt.principal.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Return the user info.
     * The user must be authenticated by JWT access token
     *
     * @param principal the authenticated user by JWT token
     * @return a user info as UserResponse.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyAccount(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        UserResponse userResponse = userService.findUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Delete the user.
     * The user must be authenticated by JWT token
     *
     * @param principal the authenticated user by JWT token
     * @return HTTP 204 No content response
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update the profile of the authenticated user.
     * The user must be authenticated by JWT token.
     *
     * @param principal the authenticated user by JWT token
     * @param request the request containing userName, profileImage to update
     * @return updated user info as UserResponse.
     */
    @PatchMapping("/me/profile")
    public ResponseEntity<UserResponse> updateMyProfile(
            Principal principal,
            @RequestBody UpdateProfileRequest request) {
        Long userId = Long.parseLong(principal.getName());
        UserResponse userResponse = userService.updateProfile(userId, request);
        return ResponseEntity.ok(userResponse);
    }
}
