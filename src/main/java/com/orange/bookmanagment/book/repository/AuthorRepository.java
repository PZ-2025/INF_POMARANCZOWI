package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>Repository class for managing {@link Author} entities.</p>
 *
 * <p>This class provides methods to save multiple authors using the underlying JPA repository.</p>
 */
@Repository
@RequiredArgsConstructor
public class AuthorRepository {

    private final AuthorJpaRepository authorJpaRepository;

    /**
     * Saves a list of author entities.
     *
     * @param authors the list of author entities to save
     * @return the saved list of author entities
     */
    public List<Author> saveAllAuthors(List<Author> authors) {
        return authorJpaRepository.saveAll(authors);
    }
}
