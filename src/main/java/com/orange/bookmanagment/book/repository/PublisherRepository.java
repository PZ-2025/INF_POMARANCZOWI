package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Publisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * <p>Repository class for managing {@link Publisher} entities.</p>
 *
 * <p>This class provides methods to save and find publishers using the underlying JPA repository.</p>
 */
@Repository
@RequiredArgsConstructor
public class PublisherRepository {
    private final PublisherJpaRepository publisherJpaRepository;

    /**
     * Saves a given publisher entity.
     *
     * @param publisher the publisher entity to save
     * @return the saved publisher entity
     */
    public Publisher savePublisher(Publisher publisher) {
        return publisherJpaRepository.save(publisher);
    }

    /**
     * Finds a publisher entity by its ID.
     *
     * @param id the ID of the publisher entity to find
     * @return the found publisher entity, or null if not found
     */
    public Publisher findPublisherById(Long id) {
        return publisherJpaRepository.findById(id).orElse(null);
    }

}
