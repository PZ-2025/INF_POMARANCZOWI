package com.orange.bookmanagment.book.web.mapper;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.web.model.BookDto;
import com.orange.bookmanagment.shared.util.TimeUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookDtoMapper {

    public BookDto toDto(Book book){
        return new BookDto(
                book.getTitle(),
                book.getAuthors(),
                book.getPublisher(),
                book.getDescription(),
                book.getGenre(),
                book.getStatus(),
                book.getCoverImage(),
                TimeUtil.getTimeInStandardFormat(book.getCreatedAt()),
                TimeUtil.getTimeInStandardFormat(book.getUpdatedAt())
        );
    }
}
