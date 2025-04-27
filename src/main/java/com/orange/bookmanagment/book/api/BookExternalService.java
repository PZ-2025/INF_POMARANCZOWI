package com.orange.bookmanagment.book.api;

import com.orange.bookmanagment.shared.enums.BookStatus;

public interface BookExternalService {
    BookInternalDto getBookForExternal(long id);
    BookStatus getBookStatusForExternal(long id);
    boolean existsBookById(long id);
    void updateBookStatus(long id, BookStatus status);
}
