package com.orange.bookmanagment.user.api;

import com.orange.bookmanagment.user.exception.UserAlreadyExistException;
import com.orange.bookmanagment.user.exception.UserNotFoundException;
import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.model.enums.UserType;
import com.orange.bookmanagment.user.web.requests.ChangePasswordRequest;
import com.orange.bookmanagment.user.web.requests.UserRegisterRequest;

import java.util.List;
import java.util.Optional;

/**
 * Interfejs zewnętrznego serwisu użytkowników, udostępniający operacje dostępne dla innych modułów.
 */
public interface UserExternalService {

    /**
     * Zwraca identyfikator użytkownika na podstawie jego adresu e-mail.
     *
     * @param email adres e-mail użytkownika
     * @return identyfikator użytkownika
     * @throws UserNotFoundException jeśli użytkownik o podanym adresie e-mail nie istnieje
     */
    long getUserIdByEmail(String email) throws UserNotFoundException;

    /**
     * Zwraca losowego bibliotekarza.
     *
     * @return Optional z losowym użytkownikiem o typie LIBRARIAN
     */
    Optional<Long> getRandomLibrarianId();
}
