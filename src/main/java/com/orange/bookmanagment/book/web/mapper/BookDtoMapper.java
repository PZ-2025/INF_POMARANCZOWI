package com.orange.bookmanagment.book.web.mapper;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.web.model.AuthorDto;
import com.orange.bookmanagment.book.web.model.BookDto;
import com.orange.bookmanagment.shared.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * BookDtoMapper is a component that provides methods to convert between Book entities and their corresponding DTOs.
 * It is used to map data between the database model and the API representation.
 */
@Component
@RequiredArgsConstructor
public class BookDtoMapper {
    private final AuthorMapper authorMapper;
    private final PublisherMapper publisherMapper;

    /**
     * Converts a Book entity to a BookDto object.
     *
     * @param book the Book entity to convert
     * @return the converted BookDto object
     */
    public BookDto toDto(Book book){
        List<AuthorDto> authorDtos = book.getAuthors().stream()
                .map(authorMapper::toDto)
                .toList();

        return new BookDto(
                book.getTitle(),
                authorDtos,
                publisherMapper.toDto(book.getPublisher()),
                book.getDescription(),
                book.getGenre(),
                book.getStatus(),
                book.getCoverImage(),
                TimeUtil.getTimeInStandardFormat(book.getCreatedAt()),
                TimeUtil.getTimeInStandardFormat(book.getUpdatedAt())
        );
    }
}
