package com.orange.bookmanagment.book.api;

import com.orange.bookmanagment.book.api.dto.BookExternalDto;
import com.orange.bookmanagment.shared.enums.BookStatus;

public interface BookExternalService {
    BookExternalDto getBookForExternal(long id);
    BookStatus getBookStatusForExternal(long id);
    boolean existsBookById(long id);
    void updateBookStatus(long id, BookStatus status);
}
