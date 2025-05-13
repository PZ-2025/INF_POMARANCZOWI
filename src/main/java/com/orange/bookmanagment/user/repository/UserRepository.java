package com.orange.bookmanagment.user.repository;

import com.orange.bookmanagment.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Warstwa dostępu do danych użytkowników z dodatkowymi metodami biznesowymi.
 * <p>
 * Wykorzystuje {@link UserJpaRepository} jako backend JPA.
 */
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final UserJpaRepository userJpaRepository;

    /**
     * Znajduje użytkownika na podstawie adresu e-mail.
     *
     * @param email adres e-mail użytkownika
     * @return Optional z użytkownikiem, jeśli istnieje
     */
    public Optional<User> findUserByEmail(String email) {
        return userJpaRepository.findUserByEmail(email);
    }

    /**
     * Znajduje użytkownika po ID.
     *
     * @param id identyfikator użytkownika
     * @return Optional z użytkownikiem, jeśli istnieje
     */
    public Optional<User> findUserById(long id) {
        return userJpaRepository.findById(id);
    }

    /**
     * Tworzy nowego użytkownika.
     *
     * @param user encja użytkownika
     * @return zapisany użytkownik
     */
    public User createUser(User user) {
        return userJpaRepository.save(user);
    }

    /**
     * Aktualizuje dane użytkownika.
     *
     * @param user encja użytkownika z nowymi danymi
     * @return zaktualizowany użytkownik
     */
    public User updateUser(User user) {
        return userJpaRepository.save(user);
    }

    /**
     * Wyszukuje użytkowników po imieniu.
     *
     * @param firstName imię użytkownika
     * @return lista użytkowników z podanym imieniem
     */
    public List<User> findUsersByFirstName(String firstName) {
        return userJpaRepository.findUsersByFirstName(firstName);
    }

    /**
     * Sprawdza, czy użytkownik istnieje po jego ID.
     *
     * @param id identyfikator użytkownika
     * @return true, jeśli użytkownik istnieje
     */
    public boolean existsById(Long id) {
        return userJpaRepository.existsById(id);
    }

    /**
     * Zwraca losowego bibliotekarza.
     *
     * @return Optional z losowym użytkownikiem o typie LIBRARIAN
     */
    public Optional<User> findRandomLibrarian() {
        return userJpaRepository.findRandomLibrarian();
    }
}
