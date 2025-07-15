package hwan.diary.service;

import hwan.diary.domain.User;
import hwan.diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void join(User user) {
        validateDuplicateUser(user);
        userRepository.save(user);
    }

    private void validateDuplicateUser(User user) {
        userRepository.findByProviderId(user.getProviderId())
                .ifPresent(u -> {
                    throw new IllegalStateException(String.format("User with provider ID %s already exists", u.getProviderId()));
                });
    }

    @Override
    public Optional<User> findMember(Long id) {
        return userRepository.findById(id);
    }
}
