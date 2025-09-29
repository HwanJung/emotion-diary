package hwan.diary.domain.auth.dto.request;

import hwan.diary.domain.user.values.Provider;

public record OAuthUserRequest(
    String username,
    Provider provider,
    String providerId,
    String email) {
}
