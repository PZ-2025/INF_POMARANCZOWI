package com.orange.bookmanagment.user.service;

import com.orange.bookmanagment.user.exception.UserAlreadyExistException;
import com.orange.bookmanagment.user.exception.UserNotFoundException;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.model.enums.UserType;
import com.orange.bookmanagment.user.web.requests.ChangePasswordRequest;
import com.orange.bookmanagment.user.web.requests.UpdateUserRequest;
import com.orange.bookmanagment.user.web.requests.UserRegisterRequest;

import java.util.List;

/**
 * Interfejs definiujący operacje związane z użytkownikami w systemie zarządzania książkami.
 */
public interface UserService {

    /**
     * Pobiera użytkownika na podstawie adresu e-mail.
     *
     * @param email adres e-mail użytkownika
     * @return obiekt użytkownika
     * @throws UserNotFoundException jeśli użytkownik nie istnieje
     */
    User getUserByEmail(String email) throws UserNotFoundException;

    /**
     * Zwraca ID użytkownika na podstawie jego adresu e-mail.
     *
     * @param email adres e-mail
     * @return ID użytkownika
     * @throws UserNotFoundException jeśli użytkownik nie istnieje
     */
    long getUserIdByEmail(String email) throws UserNotFoundException;

    /**
     * Pobiera użytkownika na podstawie ID.
     *
     * @param id ID użytkownika
     * @return obiekt użytkownika
     * @throws UserNotFoundException jeśli użytkownik nie istnieje
     */
    User getUserById(long id) throws UserNotFoundException;

    /**
     * Pobiera listę użytkowników na podstawie imienia.
     *
     * @param firstName imię użytkownika
     * @return lista użytkowników
     */
    List<User> getUserByFirstName(String firstName);

    /**
     * Rejestruje nowego użytkownika.
     *
     * @param registerRequest dane rejestracyjne
     * @param userType typ użytkownika
     * @return nowo utworzony użytkownik
     * @throws UserAlreadyExistException jeśli użytkownik już istnieje
     */
    User registerUser(UserRegisterRequest registerRequest, UserType userType) throws UserAlreadyExistException;

    /**
     * Aktualizuje dane użytkownika.
     *
     * @param user użytkownik do zaktualizowania
     * @return zaktualizowany użytkownik
     * @throws UserNotFoundException jeśli użytkownik nie istnieje
     */
    User updateUser(User user) throws UserNotFoundException;

    /**
     * Zmienia hasło użytkownika.
     *
     * @param userId ID użytkownika
     * @param changePasswordRequest dane zmiany hasła
     */
    void changeUserPassword(long userId, ChangePasswordRequest changePasswordRequest) throws UserNotFoundException;

    /**
     * Sprawdza, czy użytkownik o danym ID istnieje.
     *
     * @param id ID użytkownika
     * @return true, jeśli użytkownik istnieje
     */
    boolean existsById(long id);

    /**
     * Aktualizuje dane podstawowe użytkownika.
     *
     * @param userId ID użytkownika
     * @param request dane do aktualizacji
     */
    void updateUserData(Long userId, UpdateUserRequest request);

    /**
     * Aktualizuje ścieżkę do awatara użytkownika.
     *
     * @param userId ID użytkownika
     * @param path nowa ścieżka do pliku
     */
    void updateAvatarPath(Long userId, String path);

    /**
     * Usuwa awatar użytkownika z systemu.
     *
     * @param userId ID użytkownika
     */
    void deleteUserAvatar(Long userId);

    /**
     * Zwraca wszystkich użytkowników.
     *
     * @return lista użytkowników
     */
    List<User> getAllUsers();
}
