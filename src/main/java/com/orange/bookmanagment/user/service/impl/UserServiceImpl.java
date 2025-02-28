package com.orange.bookmanagment.user.service.impl;


import com.orange.bookmanagment.user.exception.IllegalAccountAccessException;
import com.orange.bookmanagment.user.exception.UserAlreadyExistException;
import com.orange.bookmanagment.user.exception.UserNotFoundException;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.repository.UserRepository;
import com.orange.bookmanagment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found by email"));
    }

    @Override
    public User getUserById(long id) throws UserNotFoundException {
        return userRepository.findUserById(id).orElseThrow(() -> new UserNotFoundException("User not found by id"));
    }

    @Override
    public List<User> getUserByFirstName(String firstName) {
        return userRepository.findUsersByFirstName(firstName);
    }

    @Override
    public User registerUser(User user) throws UserAlreadyExistException {
        userRepository.findUserByEmail(user.getEmail()).ifPresent(u -> {
            throw new UserAlreadyExistException("User already exists");
        });


        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(User user) throws UserNotFoundException {
        userRepository.findUserByEmail(user.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found by email"));

        return userRepository.updateUser(user);
    }

    @Override
    public void changeUserPassword(User user, String oldPassword, String newPassword) {
        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalAccountAccessException("Old password is not correct");
        }
        user.changePassword(passwordEncoder.encode(newPassword));

        updateUser(user);
    }
}
