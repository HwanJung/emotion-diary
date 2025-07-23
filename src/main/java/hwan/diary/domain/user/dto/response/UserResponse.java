package hwan.diary.domain.user.dto.response;

public record UserResponse(
        Long id,
        String username,
        String provider,
        String providerId,
        String profileImageUrl,
        String email
) {}
