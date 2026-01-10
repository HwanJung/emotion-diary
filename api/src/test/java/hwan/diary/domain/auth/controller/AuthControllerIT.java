package hwan.diary.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwan.diary.domain.auth.dto.request.OAuthUserRequest;
import hwan.diary.domain.user.entity.SnsAccount;
import hwan.diary.domain.user.entity.User;
import hwan.diary.domain.user.repository.SnsAccountRepository;
import hwan.diary.domain.user.repository.UserRepository;
import hwan.diary.domain.user.values.Provider;
import hwan.diary.support.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full E2E: filters ON (default), real Service + JPA + Redis.
 * External OAuth verification is stubbed to avoid network calls.
 */
@SpringBootTest
@AutoConfigureMockMvc // addFilters defaults to true -> security filters are active
class AuthControllerIT extends IntegrationTestBase {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired JdbcTemplate jdbc;
    @Autowired StringRedisTemplate redis;
    @Autowired UserRepository userRepository;
    @Autowired SnsAccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        // clear DB and add one user
        jdbc.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");

        User user = userRepository.save(
            User.create(
                "ExistringUser",
                "existingUser@gmail.com"
            )
        );

        accountRepository.save(
            SnsAccount.create(
                user,
                Provider.GOOGLE,
                "google-9999",
                "existingUser@gmail.com"
            )
        );

        // clear Redis
        redis.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @DisplayName("Login → filters ON: returns tokens, persists user to DB, stores refresh in Redis")
    @Test
    void login_whenFirstLogin_thenSaveUserInfoAndRefreshToken() throws Exception {
        OAuthUserRequest oAuthUserRequest = new OAuthUserRequest(
            "Hong",
            Provider.GOOGLE,
            "google-1234",
            "hong@gmail.com"
        );

        var mvcRes = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(oAuthUserRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andReturn();


        // DB state: existing user 1 + new user 1
        Integer cnt = jdbc.queryForObject(
            "select count(*) from sns_accounts where provider = ? and provider_id = ?",
            Integer.class, "GOOGLE", "google-1234");
        assertThat(cnt).isEqualTo(1);

        Long userId = jdbc.queryForObject(
            "select user_id from sns_accounts where provider=? and provider_id=?",
            Long.class, "GOOGLE", "google-1234");

        // check generated refresh token
        String refresh = om.readTree(mvcRes.getResponse().getContentAsByteArray())
            .get("refreshToken").asText();

        String refreshKey = "refresh:" + userId;
        assertThat(redis.hasKey(refreshKey)).isTrue();
        assertThat(redis.opsForValue().get(refreshKey)).isEqualTo(refresh);
        assertThat(redis.getExpire(refreshKey)).isPositive();
    }

    @DisplayName("Login Again → filters ON: returns tokens, stores refresh in Redis")
    @Test
    void login_whenNotFirstLogin_thenSaveRefreshToken() throws Exception {
        OAuthUserRequest oAuthUserRequest = new OAuthUserRequest(
            "ExistingUser",
            Provider.GOOGLE,
            "google-9999",
            "existingUser@gmail.com"
        );

        Integer before = jdbc.queryForObject(
            "select count(*) from sns_accounts where provider=? and provider_id=?",
            Integer.class, "GOOGLE", "google-9999");
        assertThat(before).isEqualTo(1);

        Long userIdBefore = jdbc.queryForObject(
            "select user_id from sns_accounts where provider=? and provider_id=?",
            Long.class, "GOOGLE", "google-9999");

        var mvcRes = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(oAuthUserRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andReturn();

        Integer after = jdbc.queryForObject(
            "select count(*) from sns_accounts where provider=? and provider_id=?",
            Integer.class, "GOOGLE", "google-9999");
        assertThat(after).isEqualTo(1);

        Long userIdAfter = jdbc.queryForObject(
            "select user_id from sns_accounts where provider=? and provider_id=?",
            Long.class, "GOOGLE", "google-9999");
        assertThat(userIdAfter).isEqualTo(userIdBefore);

        String refresh = om.readTree(mvcRes.getResponse().getContentAsByteArray())
            .get("refreshToken").asText();

        String refreshKey = "refresh:" + userIdAfter;
        assertThat(redis.hasKey(refreshKey)).isTrue();
        assertThat(redis.opsForValue().get(refreshKey)).isEqualTo(refresh);
        assertThat(redis.getExpire(refreshKey)).isPositive();
    }

    @DisplayName("Logout → filters ON: requires valid access token; clears refresh in Redis")
    @Test
    void logout_requiresAccessToken_and_clearsRefresh() throws Exception {
        // 1) login → get tokens
        OAuthUserRequest oAuthUserRequest = new OAuthUserRequest(
            "ExistingUser",
            Provider.GOOGLE,
            "google-9999",
            "existingUser@gmail.com"
        );

        var mvcRes = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(oAuthUserRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andReturn();

        Tokens tokens = om.readValue(mvcRes.getResponse().getContentAsByteArray(), Tokens.class);

        // 2) logout with Authorization: Bearer <access>
        mvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + tokens.accessToken()))
            .andExpect(status().isNoContent());

        assertThat(redis.hasKey(tokens.refreshToken())).isFalse();
        assertThat(redis.getExpire(tokens.refreshToken())).isEqualTo(-2L); // -2 means no key

    }

    @DisplayName("Logout → filters ON: missing/invalid access token → 401")
    @Test
    void logout_unauthorized_whenNoAccessToken() throws Exception {
        mvc.perform(post("/api/auth/logout"))
            .andExpect(status().isUnauthorized());
    }

    @DisplayName("Reissue → filters ON or OFF: using refresh in header returns new access")
    @Test
    void reissue_withRefreshToken() throws Exception {
        // login to get refreshToken
        OAuthUserRequest oAuthUserRequest = new OAuthUserRequest(
            "ExistingUser",
            Provider.GOOGLE,
            "google-9999",
            "existingUser@gmail.com"
        );

        var loginRes = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(oAuthUserRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andReturn();

        Tokens tokens = om.readValue(loginRes.getResponse().getContentAsByteArray(), Tokens.class);

        var reissueRes = mvc.perform(post("/api/auth/reissue")
                .header("Refresh-Token", tokens.refreshToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andReturn();

        Tokens reissuedTokens = om.readValue(reissueRes.getResponse().getContentAsByteArray(), Tokens.class);

        mvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokens.accessToken()))
            .andExpect(status().isNoContent());
    }

    /* ======== Helpers ======== */

    // DTO for response mapping
    public record Tokens(String accessToken, String refreshToken) {}
}

