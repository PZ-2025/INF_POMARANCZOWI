package com.orange.bookmanagment.user.service;

import com.orange.bookmanagment.user.exception.UserAlreadyExistException;
import com.orange.bookmanagment.user.exception.UserNotFoundException;
import com.orange.bookmanagment.user.model.User;

import java.util.List;

public interface UserService {

    User getUserByEmail(String email) throws UserNotFoundException;

    User getUserById(long id) throws UserNotFoundException;

    List<User> getUserByFirstName(String firstName);

    User registerUser(User user) throws UserAlreadyExistException;

    User updateUser(User user) throws UserNotFoundException;

    void changeUserPassword(User user, String oldPassword, String newPassword);
}
