package hwan.diary.domain.diary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwan.diary.domain.auth.dto.request.OAuthUserRequest;
import hwan.diary.domain.diary.dto.DiaryDto;
import hwan.diary.domain.diary.dto.request.CreateDiaryRequest;
import hwan.diary.domain.user.repository.UserRepository;
import hwan.diary.domain.user.values.Provider;
import hwan.diary.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class DiaryControllerIT extends IntegrationTestBase {


    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @Test
    void createDiary_whenValidInput_returnsCreatedDiary() throws Exception {
        OAuthUserRequest oAuthUserRequest = new OAuthUserRequest(
            "ExistingUser",
            Provider.GOOGLE,
            "google-9999",
            "existingUser@gmail.com"
        );

        CreateDiaryRequest createDiaryRequest = new CreateDiaryRequest(
            "extitle",
            "exontent",
            "exobjkey",
            LocalDate.of(2025, 9, 1)
        );

        var mvcRes = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(oAuthUserRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andReturn();

        DiaryControllerIT.Tokens tokens = om.readValue(mvcRes.getResponse().getContentAsByteArray(), DiaryControllerIT.Tokens.class);

        mvc.perform(post("/api/diaries")
                .header("Authorization", "Bearer " + tokens.accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(createDiaryRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.imageKey").value("exobjkey"));

    }


    /* ======== Helpers ======== */

    // DTO for response mapping
    public record Tokens(String accessToken, String refreshToken) {}
}
