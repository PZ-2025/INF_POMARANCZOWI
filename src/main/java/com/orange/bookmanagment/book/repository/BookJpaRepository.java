package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>JPA repository interface for managing {@link Book} entities.</p>
 *
 * <p>This interface provides methods to perform CRUD operations on books and custom queries.</p>
 */
@Repository
interface BookJpaRepository extends JpaRepository<Book,Long> {

    /**
     * Finds book entities by their title.
     *
     * @param title the title of the books to find
     * @return a list of book entities with the given title
     */
    List<Book> findBookByTitle(String title);
}
