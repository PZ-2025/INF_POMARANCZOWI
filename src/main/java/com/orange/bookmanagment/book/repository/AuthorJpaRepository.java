package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>JPA repository interface for managing {@link Author} entities.</p>
 *
 * <p>This interface provides methods to perform CRUD operations on authors and custom queries.</p>
 */
@Repository
interface AuthorJpaRepository extends JpaRepository<Author,Long> {

//    List<Author> findAuthorByAuthorName(String authorName);
}
