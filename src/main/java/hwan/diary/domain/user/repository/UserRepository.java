package hwan.diary.domain.user.repository;

import hwan.diary.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByProviderId(String providerID);
}
