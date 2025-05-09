package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.shared.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Book> findByStatus(BookStatus status);

    @Query(value = "SELECT * FROM books ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Book> findRandomBooks(@Param("limit") int limit);

    @Query(value = "SELECT * FROM books WHERE genre = :genre ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Book> findRandomBooksByGenre(@Param("genre") String genre, @Param("limit") int limit);

    @Query(value = "SELECT genre, COUNT(*) AS count FROM books GROUP BY genre ORDER BY count DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5Genres();

    @Query("SELECT DISTINCT b FROM Book b " +
            "JOIN b.authors a " +
            "JOIN b.publisher p " +
            "WHERE LOWER(b.title) LIKE %:query% " +
            "OR LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE %:query% " +
            "OR LOWER(p.name) LIKE %:query% " +
            "OR LOWER(b.genre) LIKE %:query%")
    List<Book> searchBooks(@Param("query") String query);
}
