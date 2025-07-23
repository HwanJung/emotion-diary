package hwan.diary.domain.user.dto.request;

public record UpdateProfileRequest(
    String username,
    String profileImageUrl
) {}
