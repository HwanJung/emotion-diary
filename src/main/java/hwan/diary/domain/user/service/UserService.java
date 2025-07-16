package hwan.diary.domain.user.service;

import hwan.diary.domain.user.entity.User;

import java.util.Optional;

public interface UserService {

    void join(User user);

    Optional<User> findMember(Long id);
}
