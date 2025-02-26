package com.orange.bookmanagment.user.repository;


import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final UserJpaRepository userJpaRepository;

    public Optional<User> findUserByEmail(String email) {
        return userJpaRepository.findUserByEmail(email);
    }

    public Optional<User> findUserById(long id){
        return userJpaRepository.findById(id);
    }

    public User createUser(User user){
        return userJpaRepository.save(user);
    }

    public User updateUser(User user){
        return userJpaRepository.save(user);
    }

    public List<User> findUsersByFirstName(String firstName){
        return userJpaRepository.findUsersByFirstName(firstName);
    }

}
