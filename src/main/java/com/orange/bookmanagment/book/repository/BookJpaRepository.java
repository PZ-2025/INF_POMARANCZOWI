package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.shared.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interfejs JPA do zarządzania encjami {@link Book}.
 * <p>
 * Zawiera metody do operacji CRUD i zapytań niestandardowych.
 */
@Repository
interface BookJpaRepository extends JpaRepository<Book,Long> {

    /**
     * Wyszukuje książki po tytule.
     *
     * @param title tytuł książki
     * @return lista książek o podanym tytule
     */
    List<Book> findBookByTitle(String title);

    /**
     * Zwraca książki o wskazanym statusie.
     *
     * @param status status książki
     * @return lista książek
     */
    List<Book> findByStatus(BookStatus status);

    /**
     * Zwraca losowe książki.
     *
     * @param limit maksymalna liczba książek
     * @return lista losowych książek
     */
    @Query(value = "SELECT * FROM books ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Book> findRandomBooks(@Param("limit") int limit);

    /**
     * Zwraca losowe książki danego gatunku.
     *
     * @param genre gatunek książki
     * @param limit maksymalna liczba wyników
     * @return lista książek pasujących do gatunku
     */
    @Query(value = "SELECT * FROM books WHERE genre = :genre ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Book> findRandomBooksByGenre(@Param("genre") String genre, @Param("limit") int limit);

    /**
     * Zwraca 5 najczęściej występujących gatunków książek.
     *
     * @return lista par [gatunek, liczba wystąpień]
     */
    @Query(value = "SELECT genre, COUNT(*) AS count FROM books GROUP BY genre ORDER BY count DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5Genres();

    /**
     * Wyszukuje książki na podstawie tytułu, autora, wydawcy lub gatunku.
     *
     * @param query tekst wyszukiwania
     * @return lista dopasowanych książek
     */
    @Query("SELECT DISTINCT b FROM Book b " +
            "JOIN b.authors a " +
            "JOIN b.publisher p " +
            "WHERE LOWER(b.title) LIKE %:query% " +
            "OR LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE %:query% " +
            "OR LOWER(p.name) LIKE %:query% " +
            "OR LOWER(b.genre) LIKE %:query%")
    List<Book> searchBooks(@Param("query") String query);
}
