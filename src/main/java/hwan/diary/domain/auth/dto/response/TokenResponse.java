package hwan.diary.domain.auth.dto.response;

public record TokenResponse(
    String tokenType,
    String accessToken,
    String refreshToken
) {
}
