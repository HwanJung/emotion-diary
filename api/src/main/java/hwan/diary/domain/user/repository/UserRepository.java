package hwan.diary.domain.user.repository;

import hwan.diary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their provider ID
     *
     * @param providerID the provider ID of user
     * @return an Optional containing the found user, or empty if none found
     */
    Optional<User> findByProviderId(String providerID);
}
