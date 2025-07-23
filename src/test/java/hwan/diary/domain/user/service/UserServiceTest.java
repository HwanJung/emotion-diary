package hwan.diary.domain.user.service;

import hwan.diary.common.exception.UserNotFoundException;
import hwan.diary.api.auth.dto.request.OAuthUserRequest;
import hwan.diary.domain.user.dto.request.UpdateProfileRequest;
import hwan.diary.domain.user.dto.response.UserResponse;
import hwan.diary.domain.user.entity.User;
import hwan.diary.domain.user.repository.UserRepository;
import hwan.diary.domain.user.values.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findOrRegister_whenUserExists_thenReturnsExistingUser() {
        // given
        String providerId = "google-1234";

        User existingUser = User.builder()
            .providerId(providerId)
            .provider(Provider.GOOGLE)
            .email("user@google.com")
            .username("user")
            .build();

        given(userRepository.findByProviderId(providerId)).willReturn(Optional.of(existingUser));

        OAuthUserRequest request = new OAuthUserRequest(
            "user",
            Provider.GOOGLE,
            providerId,
            "user@google.com"
        );

        // when
        UserResponse result = userService.findOrRegister(request);

        // then
        assertNotNull(result);
        assertEquals(providerId, result.providerId());
        assertEquals("GOOGLE", result.provider());
        assertEquals("user@google.com", result.email());
        assertEquals("user", result.username());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findOrRegister_whenUserNotExists_thenRegistersNewUser() {
        //given
        String providerId = "google-1234";

        given(userRepository.findByProviderId(providerId)).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        OAuthUserRequest request = new OAuthUserRequest(
            "user",
            Provider.GOOGLE,
            providerId,
            "user@google.com"
        );

        // when
        UserResponse result = userService.findOrRegister(request);

        // then
        assertNotNull(result);
        assertEquals(providerId, result.providerId());
        assertEquals("GOOGLE", result.provider());
        assertEquals("user@google.com", result.email());
        assertEquals("user", result.username());

        verify(userRepository).findByProviderId(providerId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_whenUserIdGiven_thenDeletesUser() {
        // given
        Long userId = 1234L;

        // when
        userService.deleteUser(userId);

        //then
        verify(userRepository).deleteById(userId);
    }

    @Test
    void findUserById_whenUserExists_thenReturnsUser() {
        // given
        Long userId = 1234L;

        User existingUser = User.builder()
            .username("user")
            .provider(Provider.GOOGLE)
            .providerId("google-1234")
            .email("user@google.com")
            .build();

        ReflectionTestUtils.setField(existingUser, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));

        // when
        UserResponse result = userService.findUserById(userId);

        // then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("user", result.username());
        assertEquals("GOOGLE", result.provider());
        assertEquals("google-1234", result.providerId());
        assertEquals("user@google.com", result.email());

        verify(userRepository).findById(userId);
    }

    @Test
    void findUserById_whenUserNotExists_thenThrowsUserNotFoundException() {
        // given
        Long userId = 1234L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        // then
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void updateProfile_whenUserExists_thenUpdatesProfile() {
        // given
        Long userId = 1234L;

        User user = User.builder()
            .username("user")
            .provider(Provider.GOOGLE)
            .providerId("google-1234")
            .email("user@google.com")
            .profileImageUrl("profile-image-url")
            .build();

        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.updateProfile(
            userId,
            new UpdateProfileRequest("new_username", "new-profile-image-url")
        );

        // then
        assertEquals("new_username", user.getUsername());
        assertEquals("new-profile-image-url", user.getProfileImageUrl());

        verify(userRepository).findById(userId);
    }

    @Test
    void updateProfile_whenUserNotExists_thenUpdatesProfile() {
        // given
        Long userId = 1234L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        // then
        assertThrows(
            UserNotFoundException.class,
            () -> userService.updateProfile(userId, new UpdateProfileRequest("new_username", "new-profile-image-url"))
        );

        verify(userRepository).findById(userId);
    }

}
