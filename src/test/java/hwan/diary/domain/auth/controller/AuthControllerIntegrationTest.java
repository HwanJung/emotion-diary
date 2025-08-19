package hwan.diary.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwan.diary.domain.auth.dto.request.OAuthUserRequest;
import hwan.diary.domain.user.values.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
//@ActiveProfiles("test")
//@Testcontainers
@AutoConfigureMockMvc(addFilters = true)
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_whenNeedToRegister_thenReturnsAccessTokenAndRefreshToken() throws Exception {
        OAuthUserRequest request = new OAuthUserRequest(
            "testname",
            Provider.GOOGLE,
            "google-1234",
            "test@google.com"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.accessToken").exists());
    }


}
