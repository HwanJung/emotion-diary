package hwan.diary.domain.user.dto.response;

public record UserResponse(
        Long id,
        String username,
        String profileImageKey,
        String email
) {}
