package hwan.diary.domain.user.util;

import hwan.diary.domain.user.dto.response.UserResponse;
import hwan.diary.domain.user.entity.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getProfileImageKey(),
            user.getEmail()
        );
    }
}
