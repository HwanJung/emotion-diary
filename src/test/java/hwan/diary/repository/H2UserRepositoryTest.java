package hwan.diary.repository;

import hwan.diary.domain.SNS;
import hwan.diary.domain.User;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

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

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("hwan");
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
