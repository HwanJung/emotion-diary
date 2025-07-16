package hwan.diary.repository;

import hwan.diary.domain.user.values.SNS;
import hwan.diary.domain.user.entity.User;
import hwan.diary.domain.user.repository.H2UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class H2UserRepositoryTest {

    @Autowired
    private EntityManager em;

    private H2UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new H2UserRepository(em);
    }

    @Test
    void 사용자_저장후_ID로_조회() {
        // given
        User user = createTestUser();
        // when
        userRepository.save(user);

        Optional<User> result = userRepository.findById(user.getId());
        assertTrue(result.isPresent());

        String expectedName = "hwan";
        String actualName   = result.get().getUsername();

        // then
        assertEquals(expectedName, actualName);
    }

    private User createTestUser() {
        User user = new User();
        user.setUsername("hwan");
        user.setProvider(SNS.GOOGLE);
        user.setProviderId("asd123");
        user.setProfileImageUrl("https://hwan.google.com");
        user.setEmail("hwan@gmail.com");

        return user;
    }
}
