package com.orange.bookmanagment.user.service.impl;

import com.orange.bookmanagment.user.api.UserExternalService;
import com.orange.bookmanagment.user.exception.IllegalAccountAccessException;
import com.orange.bookmanagment.user.exception.UserAlreadyExistException;
import com.orange.bookmanagment.user.exception.UserNotFoundException;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.model.enums.UserType;
import com.orange.bookmanagment.user.repository.UserRepository;
import com.orange.bookmanagment.user.service.UserService;
import com.orange.bookmanagment.user.web.requests.ChangePasswordRequest;
import com.orange.bookmanagment.user.web.requests.UpdateUserRequest;
import com.orange.bookmanagment.user.web.requests.UserRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService, UserExternalService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found by email"));
    }

    @Override
    public long getUserIdByEmail(String email) throws UserNotFoundException {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found by email")).getId();
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
    @Transactional
    public User registerUser(UserRegisterRequest userRegisterRequest, UserType userType) throws UserAlreadyExistException {
        userRepository.findUserByEmail(userRegisterRequest.email()).ifPresent(u -> {
            throw new UserAlreadyExistException("User already exists");
        });

        return userRepository.createUser(new User(passwordEncoder.encode(userRegisterRequest.password()), userRegisterRequest.email(),userRegisterRequest.lastName(),userRegisterRequest.firstName(), userType));
    }

    @Override
    public User updateUser(User user) throws UserNotFoundException {
        userRepository.findUserByEmail(user.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found by email"));

        return userRepository.updateUser(user);
    }

    @Override
    @Transactional
    public void changeUserPassword(long userId, ChangePasswordRequest changePasswordRequest) {
        final User user = getUserById(userId);

        if (!passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            throw new IllegalAccountAccessException("Old password is not correct");
        }

        user.changePassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
        updateUser(user);
    }

    @Override
    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    @Override
    public void updateUserData(Long userId, UpdateUserRequest request) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Użytkownik nie istnieje"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        userRepository.updateUser(user);
    }
}
