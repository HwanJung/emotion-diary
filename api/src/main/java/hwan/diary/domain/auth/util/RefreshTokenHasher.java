package hwan.diary.security.jwt.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public final class RefreshTokenHasher {

    private RefreshTokenHasher() {}

    public static String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            // byte to String
            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(hashed);

        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
