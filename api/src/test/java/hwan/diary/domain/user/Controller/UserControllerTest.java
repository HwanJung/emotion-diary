package hwan.diary.domain.user.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwan.diary.domain.user.controller.UserController;
import hwan.diary.domain.user.dto.request.UpdateProfileRequest;
import hwan.diary.domain.user.dto.response.UserResponse;
import hwan.diary.domain.user.service.UserService;
import hwan.diary.security.jwt.principal.JwtAuthenticationToken;
import hwan.diary.security.jwt.principal.JwtUserPrincipal;
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
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UserControllerTest.TestMockConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class TestMockConfig {
        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }
    }

    static ObjectMapper objectMapper = new ObjectMapper();

    private UserResponse createUserResponse() {
        return new UserResponse(
            1L,
            "user",
            "profile.jpg",
            "user@google.com"
        );
    }

    @Test
    void getMyAccount_ReturnsUserResponse() throws Exception {
        // given
        UserResponse userResponse = createUserResponse();
        given(userService.findUserById(1L)).willReturn(userResponse);

        // when & then
        mockMvc.perform(get("/api/users/me")
                .principal(() -> "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.username").value("user"))
            .andExpect(jsonPath("$.profileImageKey").value("profile.jpg"))
            .andExpect(jsonPath("$.email").value("user@google.com"));

    }

    @Test
    void deleteMyAccount_ReturnsNoContent() throws Exception {
        // given

        // when & then
        mockMvc.perform(delete("/api/users/me")
                .principal(new JwtAuthenticationToken(new JwtUserPrincipal(1L))))
            .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void updateMyProfile_ReturnsUserResponse() throws Exception {
        // given
        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest(
            "updated_username",
            "updated_profile.jpg"
        );
        UserResponse userResponse = new UserResponse(
            1L,
            "updated_username",
            "updated_profile.jpg",
            "user@google.com"
        );

        given(userService.updateProfile(1L, updateProfileRequest)).willReturn(userResponse);

        // when & then
        mockMvc.perform(patch("/api/users/me/profile")
                .principal(new JwtAuthenticationToken(new JwtUserPrincipal(1L)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProfileRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.username").value("updated_username"))
            .andExpect(jsonPath("$.profileImageKey").value("updated_profile.jpg"))
            .andExpect(jsonPath("$.email").value("user@google.com"));
    }



}
