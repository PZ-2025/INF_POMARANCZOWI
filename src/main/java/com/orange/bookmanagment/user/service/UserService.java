package com.orange.bookmanagment.user.service;

import com.orange.bookmanagment.user.model.User;

public interface UserService {

    User getUserByEmail(String email);

    User getUserById(int id);

    User getUserByFirstName(String firstName);
}
