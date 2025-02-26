package com.orange.bookmanagment.user.repository;

import com.orange.bookmanagment.user.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByFirstName(String firstName);


}
