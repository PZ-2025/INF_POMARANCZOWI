package com.orange.bookmanagment.book.web.mapper;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.web.model.AuthorDto;
import com.orange.bookmanagment.book.web.model.BookDto;
import com.orange.bookmanagment.shared.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookDtoMapper {

    private final AuthorMapper authorMapper;

    public BookDto toDto(Book book){
        List<AuthorDto> authorDtos = book.getAuthors().stream()
                .map(authorMapper::toDto)
                .toList();
        return new BookDto(
                book.getTitle(),
                authorDtos,
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
