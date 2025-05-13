package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfejs JPA do operacji CRUD na encjach {@link Author}.
 */
@Repository
interface AuthorJpaRepository extends JpaRepository<Author,Long> {
    // Możliwość dodania własnych metod zapytań
}
