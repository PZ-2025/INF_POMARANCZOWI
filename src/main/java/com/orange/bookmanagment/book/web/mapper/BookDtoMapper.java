package com.orange.bookmanagment.book.web.mapper;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.web.model.AuthorDto;
import com.orange.bookmanagment.book.web.model.BookDto;
import com.orange.bookmanagment.shared.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Komponent odpowiedzialny za mapowanie encji {@link Book} do obiektu DTO {@link BookDto}.
 * Używany do reprezentacji danych książki w odpowiedziach API.
 */
@Component
@RequiredArgsConstructor
public class BookDtoMapper {

    private final AuthorMapper authorMapper;
    private final PublisherMapper publisherMapper;

    /**
     * Konwertuje encję książki na obiekt DTO.
     *
     * @param book encja książki
     * @return DTO książki
     */
    public BookDto toDto(Book book){
        List<AuthorDto> authorDtos = book.getAuthors().stream()
                .map(authorMapper::toDto)
                .toList();

        return new BookDto(
                book.getId(),
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
