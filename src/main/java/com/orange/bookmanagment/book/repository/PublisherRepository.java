package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Publisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Repozytorium do zarządzania encjami {@link Publisher}.
 * <p>
 * Umożliwia zapisywanie i pobieranie wydawców z bazy danych.
 */
@Repository
@RequiredArgsConstructor
public class PublisherRepository {

    private final PublisherJpaRepository publisherJpaRepository;

    /**
     * Zapisuje wydawcę w bazie danych.
     *
     * @param publisher encja wydawcy do zapisania
     * @return zapisana encja wydawcy
     */
    public Publisher savePublisher(Publisher publisher) {
        return publisherJpaRepository.save(publisher);
    }

    /**
     * Wyszukuje wydawcę po jego ID.
     *
     * @param id identyfikator wydawcy
     * @return encja wydawcy lub null, jeśli nie znaleziono
     */
    public Publisher findPublisherById(Long id) {
        return publisherJpaRepository.findById(id).orElse(null);
    }
}
