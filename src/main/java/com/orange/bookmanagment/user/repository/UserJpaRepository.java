package com.orange.bookmanagment.user.repository;

import com.orange.bookmanagment.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfejs JPA do zarządzania encją {@link User}.
 * <p>
 * Zawiera predefiniowane oraz niestandardowe zapytania.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<User,Long> {

    /**
     * Znajduje użytkownika na podstawie adresu e-mail.
     *
     * @param email adres e-mail użytkownika
     * @return Optional z użytkownikiem, jeśli istnieje
     */
    Optional<User> findUserByEmail(String email);


    /**
     * Wyszukuje użytkowników o podanym imieniu.
     *
     * @param firstName imię użytkownika
     * @return lista użytkowników z danym imieniem
     */
    List<User> findUsersByFirstName(String firstName);

    /**
     * Zwraca losowego użytkownika o typie LIBRARIAN.
     *
     * @return Optional z losowym bibliotekarzem
     */
    @Query(value = "SELECT * FROM users WHERE user_type = 'LIBRARIAN' ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<User> findRandomLibrarian();
}
