package com.orange.bookmanagment.book.api;

import com.orange.bookmanagment.book.api.dto.BookExternalDto;
import com.orange.bookmanagment.shared.enums.BookStatus;

import java.util.List;

/**
 * Serwis zewnętrzny do zarządzania książkami.
 * Umożliwia pobieranie informacji o książkach oraz zmianę ich statusu.
 */
public interface BookExternalService {

    /**
     * Zwraca dane książki na podstawie jej identyfikatora.
     *
     * @param id identyfikator książki
     * @return DTO zawierające dane książki
     */
    BookExternalDto getBookForExternal(long id);

    /**
     * Zwraca status książki na podstawie jej identyfikatora.
     *
     * @param id identyfikator książki
     * @return status książki
     */
    BookStatus getBookStatusForExternal(long id);

    /**
     * Sprawdza, czy książka o podanym identyfikatorze istnieje.
     *
     * @param id identyfikator książki
     * @return true, jeśli książka istnieje; false w przeciwnym razie
     */
    boolean existsBookById(long id);

    /**
     * Aktualizuje status książki.
     *
     * @param id identyfikator książki
     * @param status nowy status książki
     */
    void updateBookStatus(long id, BookStatus status);

    /**
     * Pobiera wszystkie książki.
     *
     * @return lista wszystkich książek
     */
    List<BookExternalDto> getAllBooks();

    /**
     * Pobiera wszystkie książki o określonym statusie.
     *
     * @param status status książki
     * @return lista książek o określonym statusie
     */
    List<BookExternalDto> getBooksByStatus(BookStatus status);
}
