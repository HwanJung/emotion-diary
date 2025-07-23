package hwan.diary.domain.user.service;

import hwan.diary.common.exception.UserNotFoundException;
import hwan.diary.api.auth.dto.request.OAuthUserRequest;
import hwan.diary.domain.user.dto.request.UpdateProfileRequest;
import hwan.diary.domain.user.dto.response.UserResponse;
import hwan.diary.domain.user.entity.User;
import hwan.diary.domain.user.repository.UserRepository;
import hwan.diary.domain.user.util.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Find existing user by providerId, or register if not found.
     *
     * @param request OAuth login request containing provider info and user details
     * @return the found or registered user as UserResponse
     */
    public UserResponse findOrRegister(OAuthUserRequest request) {

        User user = userRepository.findByProviderId(request.providerId())
            .orElseGet(() -> userRepository.save(
                User.builder()
                    .username(request.username())
                    .provider(request.provider())
                    .providerId(request.providerId())
                    .email(request.email())
                    .build()
            ));

        return UserMapper.toResponse(user);
    }

    /**
     * Delete a user by ID
     *
     * @param id the ID of the user to delete
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * find a user by ID
     *
     * @param id the id of user to find
     * @return the found user as UserResponse
     */

    public UserResponse findUserById(Long id) {
        User user = this.findUserByIdInternal(id);

        if(user == null) {
            throw new UserNotFoundException(id.toString());
        }

        return UserMapper.toResponse(user);
    }

    /**
     * Update user information(username, profile image)
     *
     * @param id the id of the user to update
     * @param request the request containing username, profileImageUrl to set
     *
     * @return the updated user as UserResponse
     */
    public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
        User user = this.findUserByIdInternal(id);

        if(user == null) {
            throw new UserNotFoundException(id.toString());
        }

        user.setUsername(request.username());
        user.setProfileImageUrl(request.profileImageUrl());

        return UserMapper.toResponse(user);
    }

    /**
     * Internal private method.
     * Find a user by id. if not exist, throw UserNotFoundException
     *
     * @param id the id of the user to update
     * @return the found user entity
     */
    private User findUserByIdInternal(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
