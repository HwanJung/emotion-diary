package hwan.diary.repository;

import hwan.diary.domain.User;

import java.util.Optional;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByProviderID(String providerID);
}
