package hwan.diary.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwan.diary.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full E2E: filters ON (default), real Service + JPA + Redis.
 * External OAuth verification is stubbed to avoid network calls.
 */
@SpringBootTest
@AutoConfigureMockMvc // addFilters defaults to true -> security filters are active
class AuthControllerE2EIT extends IntegrationTestSupport {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired JdbcTemplate jdbc;
    @Autowired StringRedisTemplate redis;

    @BeforeEach
    void flushRedis() {
        redis.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    /* ==== Stub external OAuth verification (replace with your real type) ==== */
    interface OAuthIdTokenVerifier {
        OAuthProfile verify(String provider, String idToken);
    }
    static class OAuthProfile {
        final String provider; // e.g. GOOGLE
        final String subject;  // provider's "sub"
        final String email;
        OAuthProfile(String provider, String subject, String email) {
            this.provider = provider; this.subject = subject; this.email = email;
        }
    }
    @TestConfiguration
    static class StubOAuthVerifierConfig {
        @Bean @Primary
        OAuthIdTokenVerifier stubVerifier() {
            return (provider, idToken) -> new OAuthProfile("GOOGLE", "sub-123", "hwan@example.com");
        }
    }
    // NOTE: 위 인터페이스/빈은 예시야. 네 Service가 주입받는 실제 컴포넌트 타입으로 바꾸면 됨.
    // 예) @MockBean GoogleIdTokenVerifier google; when(google.verify(...)).thenReturn(...)

    /* ======== Tests ======== */

    @DisplayName("Login → filters ON: returns tokens, persists user to DB, stores refresh in Redis")
    @Test
    void login_end_to_end() throws Exception {
        var loginBody = """
            {"provider":"GOOGLE","idToken":"dummy-id-token"}
        """;

        var mvcRes = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andReturn();

        Tokens tokens = om.readValue(mvcRes.getResponse().getContentAsByteArray(), Tokens.class);
        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();

        // DB state: user persisted (adjust table/column names to your schema)
        Integer cnt = jdbc.queryForObject(
            "select count(*) from users where provider = ? and provider_id = ?",
            Integer.class, "GOOGLE", "sub-123");
        assertThat(cnt).isEqualTo(1);

        // Redis state: at least one key created (use exact key pattern if you know it)
        Set<String> keys = redis.keys("*");
        assertThat(keys).isNotNull();
        assertThat(keys.size()).isGreaterThan(0);
    }

    @DisplayName("Logout → filters ON: requires valid access token; clears refresh in Redis")
    @Test
    void logout_requiresAccessToken_and_clearsRefresh() throws Exception {
        // 1) login → get tokens
        Tokens t = login();

        // 2) logout with Authorization: Bearer <access>
        mvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + t.accessToken()))
            .andExpect(status().isNoContent());

        // 3) reissue with old refresh should now fail (adjust status by your GlobalExceptionHandler)
        mvc.perform(post("/api/auth/reissue")
                .header("Authorization", "Bearer " + t.refreshToken()))
            .andExpect(status().is4xxClientError());
    }

    @DisplayName("Logout → filters ON: missing/invalid access token → 401")
    @Test
    void logout_unauthorized_whenNoAccessToken() throws Exception {
        mvc.perform(post("/api/auth/logout"))
            .andExpect(status().isUnauthorized()); // or 403 depending on your config
    }

    @DisplayName("Reissue → filters ON or OFF: using refresh in header returns new access")
    @Test
    void reissue_withRefreshToken() throws Exception {
        Tokens t = login();

        mvc.perform(post("/api/auth/reissue")
                .header("Authorization", "Bearer " + t.refreshToken())) // adjust if you use cookie
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists());
    }

    /* ======== Helpers ======== */

    private Tokens login() throws Exception {
        var res = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {"provider":"GOOGLE","idToken":"dummy-id-token"}
                        """))
            .andExpect(status().isOk())
            .andReturn();
        return om.readValue(res.getResponse().getContentAsByteArray(), Tokens.class);
    }

    // DTO for response mapping
    public record Tokens(String accessToken, String refreshToken) {}
}

