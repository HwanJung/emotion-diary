package hwan.diary.repository;

import hwan.diary.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public class H2UserRepository implements UserRepository {

    private final EntityManager em;

    @Autowired
    public H2UserRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(User user) {
        em.persist(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = em.find(User.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByProviderId(String providerId) {
        User user = em.find(User.class, providerId);
        return Optional.ofNullable(user);
    }
}
