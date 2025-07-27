package hwan.diary.domain.auth.dto.response;

public record AccessTokenResponse(
    String tokenType,
    String accessToken
) {
}
