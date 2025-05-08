package com.orange.bookmanagment.book.api;

import com.orange.bookmanagment.book.api.dto.BookExternalDto;
import com.orange.bookmanagment.shared.enums.BookStatus;

import java.util.List;

public interface BookExternalService {
    BookExternalDto getBookForExternal(long id);
    BookStatus getBookStatusForExternal(long id);
    boolean existsBookById(long id);
    void updateBookStatus(long id, BookStatus status);

    /**
     * Pobiera wszystkie książki
     *
     * @return lista wszystkich książek
     */
    List<BookExternalDto> getAllBooks();

    /**
     * Pobiera wszystkie książki o określonym statusie
     *
     * @param status status książek
     * @return lista książek o określonym statusie
     */
    List<BookExternalDto> getBooksByStatus(BookStatus status);
}
