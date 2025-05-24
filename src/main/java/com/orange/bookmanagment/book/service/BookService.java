package com.orange.bookmanagment.book.service;

import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.web.model.BookDto;
import com.orange.bookmanagment.shared.enums.BookStatus;
import com.orange.bookmanagment.book.web.requests.BookCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Serwis do zarządzania książkami.
 * <p>
 * Definiuje operacje związane z tworzeniem, pobieraniem, aktualizacją i wyszukiwaniem książek.
 */
public interface BookService {

    /**
     * Tworzy nową książkę.
     *
     * @param bookCreateRequest dane nowej książki
     * @return utworzona encja książki
     */
    Book createBook(BookCreateRequest bookCreateRequest);

    /**
     * Zwraca książkę o podanym ID.
     *
     * @param id identyfikator książki
     * @return znaleziona książka
     * @throws BookNotFoundException jeśli książka nie istnieje
     */
    Book getBookById(long id) throws BookNotFoundException;

    /**
     * Zwraca wszystkie książki w formie stronicowanej.
     *
     * @param pageable dane dotyczące paginacji
     * @return strona książek
     */
    Page<Book> getAllBooks(Pageable pageable);

    /**
     * Zwraca wszystkie książki bez paginacji.
     *
     * @return lista wszystkich książek
     */
    List<Book> getAllBooksUnpaged();

    /**
     * Zwraca książki o podanym tytule.
     *
     * @param title tytuł książki
     * @return lista pasujących książek
     */
    List<Book> getBookByTitle(String title);

    /**
     * Aktualizuje dane książki.
     *
     * @param book książka do aktualizacji
     * @return zaktualizowana książka
     * @throws BookNotFoundException jeśli książka nie istnieje
     */
    Book updateBook(Book book) throws BookNotFoundException;

    /**
     * Sprawdza istnienie książki po ID.
     *
     * @param id identyfikator książki
     * @return true, jeśli istnieje; false w przeciwnym razie
     */
    boolean existsById(long id);

    /**
     * Zwraca status książki po jej ID.
     *
     * @param id identyfikator książki
     * @return status książki
     */
    BookStatus getBookStatusById(long id);

    /**
     * Zwraca losowe książki w formie DTO.
     *
     * @param limit liczba książek do pobrania
     * @return lista książek DTO
     */
    List<BookDto> getRandomBooks(int limit);

    /**
     * Zwraca losowe książki dla wybranego gatunku.
     *
     * @param genre gatunek książki
     * @param limit liczba książek do pobrania
     * @return lista książek DTO
     */
    List<BookDto> getRandomBooksByGenre(String genre, int limit);

    /**
     * Zwraca 5 najczęściej występujących gatunków książek.
     *
     * @return lista nazw gatunków
     */
    List<String> getTop5Genres();

    /**
     * Wyszukuje książki po zapytaniu tekstowym.
     *
     * @param query fraza wyszukiwania
     * @return lista dopasowanych książek
     */
    List<Book> searchBooks(String query);

    List<Book> getLostBooks();
}
