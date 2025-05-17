package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repozytorium do zarządzania encjami {@link Author}.
 * <p>
 * Umożliwia zapisywanie wielu autorów jednocześnie.
 */
@Repository
@RequiredArgsConstructor
public class AuthorRepository {

    private final AuthorJpaRepository authorJpaRepository;

    /**
     * Zapisuje listę autorów w bazie danych.
     *
     * @param authors lista autorów do zapisania
     * @return zapisane encje autorów
     */
    public List<Author> saveAllAuthors(List<Author> authors) {
        return authorJpaRepository.saveAll(authors);
    }

    /**
     * Zwraca listę wszystkich autorów zapisanych w bazie danych.
     *
     * @return lista autorów
     */
    public List<Author> findAll() {
        return authorJpaRepository.findAll();
    }
}
