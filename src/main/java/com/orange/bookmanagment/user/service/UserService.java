package com.orange.bookmanagment.user.service;

import com.orange.bookmanagment.user.model.User;

import java.util.List;

public interface UserService {

    User getUserByEmail(String email);

    User getUserById(long id);

    List<User> getUserByFirstName(String firstName);

    User registerUser(User user);

    User updateUser(User user);


}
