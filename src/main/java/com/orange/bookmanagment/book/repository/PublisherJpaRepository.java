package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfejs JPA do operacji CRUD na encjach {@link Publisher}.
 */
@Repository
public interface PublisherJpaRepository extends JpaRepository<Publisher, Long> {
    // Możliwość dodania własnych metod zapytań
}
