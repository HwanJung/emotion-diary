package hwan.diary.domain.user.Controller;

import hwan.diary.domain.user.controller.UserController;
import hwan.diary.domain.user.dto.response.UserResponse;
import hwan.diary.domain.user.service.UserService;
import hwan.diary.security.jwt.principal.JwtUserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
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

    private UserResponse createUserResponse() {
        return new UserResponse(
            1L,
            "user",
            "GOOGLE",
            "google-1234",
            "profile.jpg",
            "user@google.com"
        );

    }

    @Test
    void getMyAccount_ReturnsUserInfo() throws Exception {
        // given
        UserResponse userResponse = createUserResponse();
        given(userService.findUserById(1L)).willReturn(userResponse);

        // when & then
        mockMvc.perform(get("api/users/me")
            .requestAttr("jwtUserPrincipal", new JwtUserPrincipal(1L)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.username").value("user"))
            .andExpect(jsonPath("$.provider").value("GOOGLE"))
            .andExpect(jsonPath("$.providerId").value("google-1234"))
            .andExpect(jsonPath("$.providerImageUrl").value("profile.jpg"))
            .andExpect(jsonPath("$.email").value("user@google.com"));

    }

    @Test
    void getMyAccount_User

}
