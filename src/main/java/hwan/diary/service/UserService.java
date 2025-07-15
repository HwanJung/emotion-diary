package hwan.diary.service;

import hwan.diary.domain.User;
import hwan.diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface UserService {

    void join(User user);

    Optional<User> findMember(Long id);
}
