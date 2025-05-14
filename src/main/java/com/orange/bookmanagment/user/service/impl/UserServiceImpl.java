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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Implementacja serwisu {@link UserService} oraz interfejsu zewnętrznego {@link UserExternalService}.
 * <p>
 * Obsługuje rejestrację, wyszukiwanie, aktualizację i zarządzanie kontami użytkowników.
 */
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService, UserExternalService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Pobiera użytkownika na podstawie adresu e-mail.
     *
     * @param email adres e-mail użytkownika
     * @return znaleziony użytkownik
     * @throws UserNotFoundException jeśli użytkownik nie istnieje
     */
    @Override
    public User getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found by email"));
    }

    /**
     * Zwraca identyfikator użytkownika na podstawie adresu e-mail.
     *
     * @param email adres e-mail
     * @return ID użytkownika
     * @throws UserNotFoundException jeśli użytkownik nie istnieje
     */
    @Override
    public long getUserIdByEmail(String email) throws UserNotFoundException {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found by email")).getId();
    }

    /**
     * Pobiera użytkownika na podstawie jego ID.
     *
     * @param id identyfikator użytkownika
     * @return znaleziony użytkownik
     * @throws UserNotFoundException jeśli użytkownik nie istnieje
     */
    @Override
    public User getUserById(long id) throws UserNotFoundException {
        return userRepository.findUserById(id).orElseThrow(() -> new UserNotFoundException("User not found by id"));
    }

    /**
     * Wyszukuje użytkowników na podstawie imienia.
     *
     * @param firstName imię użytkownika
     * @return lista pasujących użytkowników
     */
    @Override
    public List<User> getUserByFirstName(String firstName) {
        return userRepository.findUsersByFirstName(firstName);
    }

    /**
     * Rejestruje nowego użytkownika w systemie.
     *
     * @param userRegisterRequest dane rejestracyjne użytkownika
     * @param userType typ użytkownika (np. LIBRARIAN, READER)
     * @return nowo utworzony użytkownik
     * @throws UserAlreadyExistException jeśli użytkownik już istnieje
     */
    @Override
    @Transactional
    public User registerUser(UserRegisterRequest userRegisterRequest, UserType userType) throws UserAlreadyExistException {
        userRepository.findUserByEmail(userRegisterRequest.email()).ifPresent(u -> {
            throw new UserAlreadyExistException("User already exists");
        });

        return userRepository.createUser(new User(passwordEncoder.encode(userRegisterRequest.password()), userRegisterRequest.email(),userRegisterRequest.lastName(),userRegisterRequest.firstName(), userType));
    }

    /**
     * Aktualizuje dane użytkownika.
     *
     * @param user użytkownik do zaktualizowania
     * @return zaktualizowany obiekt użytkownika
     * @throws UserNotFoundException jeśli użytkownik nie istnieje
     */
    @Override
    public User updateUser(User user) throws UserNotFoundException {
        userRepository.findUserByEmail(user.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found by email"));

        return userRepository.updateUser(user);
    }

    /**
     * Zmienia hasło użytkownika.
     *
     * @param userId ID użytkownika
     * @param changePasswordRequest dane do zmiany hasła (stare i nowe hasło)
     * @throws IllegalAccountAccessException jeśli stare hasło jest nieprawidłowe
     */
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

    /**
     * Sprawdza, czy użytkownik o podanym ID istnieje.
     *
     * @param id identyfikator użytkownika
     * @return true, jeśli użytkownik istnieje
     */
    @Override
    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    /**
     * Aktualizuje podstawowe dane użytkownika (imię, nazwisko).
     *
     * @param userId ID użytkownika
     * @param request dane do aktualizacji
     */
    @Override
    public void updateUserData(Long userId, UpdateUserRequest request) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not exists"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        userRepository.updateUser(user);
    }

    /**
     * Aktualizuje ścieżkę do awatara użytkownika.
     *
     * @param userId ID użytkownika
     * @param path ścieżka do nowego awatara
     */
    @Override
    public void updateAvatarPath(Long userId, String path) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatarPath(path);
        userRepository.updateUser(user);
    }

    /**
     * Usuwa awatar użytkownika z systemu i czyści ścieżkę w bazie danych.
     *
     * @param userId ID użytkownika
     */
    @Override
    public void deleteUserAvatar(Long userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getAvatarPath() != null) {
            Path path = Paths.get("uploads", "avatars", "user-" + userId + ".jpg");
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setAvatarPath(null);
            userRepository.updateUser(user);
        }
    }

    @Override
    public Optional<Long> getRandomLibrarianId() {
        return userRepository.findRandomLibrarian().map(User::getId);
    }
}
