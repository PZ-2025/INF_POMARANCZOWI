package com.orange.bookmanagment.user.service;

import com.orange.bookmanagment.user.exception.UserAlreadyExistException;
import com.orange.bookmanagment.user.exception.UserNotFoundException;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.model.enums.UserType;
import com.orange.bookmanagment.user.web.requests.ChangePasswordRequest;
import com.orange.bookmanagment.user.web.requests.UserRegisterRequest;

import java.util.List;

public interface UserService {

    User getUserByEmail(String email) throws UserNotFoundException;

    User getUserById(long id) throws UserNotFoundException;

    List<User> getUserByFirstName(String firstName);

    User registerUser(UserRegisterRequest registerRequest, UserType userType) throws UserAlreadyExistException;

    User updateUser(User user) throws UserNotFoundException;

    void changeUserPassword(long userId, ChangePasswordRequest changePasswordRequest) throws UserNotFoundException;
}
