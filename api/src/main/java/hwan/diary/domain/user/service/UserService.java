package hwan.diary.domain.user.service;

import hwan.diary.common.exception.user.UserNotFoundException;
import hwan.diary.common.exception.ErrorCode;
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
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND, id);
        }

        return UserMapper.toResponse(user);
    }

    /**
     * Update user information(username, profile image)
     *
     * @param id      the id of the user to update
     * @param request the request containing username and profileImageKey to set
     * @return the updated user as UserResponse
     */
    public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND, id);
        }

        user.updateProfile(request.username(), request.profileImageKey());

        return UserMapper.toResponse(user);
    }
}
