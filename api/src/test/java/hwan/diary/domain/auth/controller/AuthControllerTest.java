package hwan.diary.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwan.diary.domain.auth.dto.request.OAuthUserRequest;
import hwan.diary.domain.auth.dto.response.AccessTokenResponse;
import hwan.diary.domain.auth.dto.response.TokenResponse;
import hwan.diary.domain.auth.service.AuthService;
import hwan.diary.domain.user.values.Provider;
import hwan.diary.security.jwt.principal.JwtAuthenticationToken;
import hwan.diary.security.jwt.principal.JwtUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AuthControllerTest.TestMockConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestMockConfig {
        @Bean
        public AuthService authService() {
            return mock(AuthService.class);
        }
    }

    @Test
    void login_whenOAuthUserRequestValid_thenReturnToken() throws Exception {
        // given
        OAuthUserRequest request = new OAuthUserRequest(
            "username",
            Provider.GOOGLE,
            "google-1234",
            "example@google.com"
        );

        TokenResponse tokenResponse = new TokenResponse(
            "Bearer",
            "access-token",
            "refresh-token"
        );

        given(authService.loginOrSignUp(any(OAuthUserRequest.class))).willReturn(tokenResponse);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void logout_whenAuthenticated_thenReturnNoContent() throws Exception {
        // given
        Long userId = 1L;

        // when & then
        mockMvc.perform(post("/api/auth/logout")
                .principal(new JwtAuthenticationToken((new JwtUserPrincipal(userId)))))
            .andExpect(status().isNoContent());

        verify(authService).logout(userId);
    }

    @Test
    void reissue_whenValidToken_thenReturnToken() throws Exception {
        // given
        TokenResponse tokenResponse = new TokenResponse(
            "Bearer",
            "access-token",
            "new-refresh-token"
        );
        String refreshToken = "refresh_token";

        given(authService.reissueAccessToken(refreshToken)).willReturn(tokenResponse);

        // when & then
        mockMvc.perform(post("/api/auth/reissue")
            .header("Refresh-Token", refreshToken)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));

    }
}
