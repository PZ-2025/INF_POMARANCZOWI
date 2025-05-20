package com.orange.bookmanagment.book.service.mapper;

import com.orange.bookmanagment.book.api.dto.AuthorExternalDto;
import com.orange.bookmanagment.book.api.dto.BookExternalDto;
import com.orange.bookmanagment.book.api.dto.PublisherExternalDto;
import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.Publisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper odpowiedzialny za konwersję encji {@link Book} do DTO {@link BookExternalDto},
 * oraz mapowanie powiązanych encji autorów i wydawcy.
 */
@Component
public class BookInternalMapper {
    /**
     * Mapuje encję książki na DTO do użytku zewnętrznego.
     *
     * @param book książka do zmapowania
     * @return DTO książki lub null, jeśli przekazano null
     */
    public BookExternalDto toDto(Book book) {
        if (book == null) {
            return null;
        }

        return new BookExternalDto(
                book.getId(),
                book.getTitle(),
                mapAuthorsToDto(book.getAuthors()),
                mapPublisherToDto(book.getPublisher()),
                book.getDescription(),
                book.getGenre(),
                book.getStatus(),
                book.getCoverImage(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }

    /**
     * Mapuje listę encji autorów na listę DTO autorów.
     *
     * @param authors lista encji autorów
     * @return lista DTO autorów
     */
    private List<AuthorExternalDto> mapAuthorsToDto(List<Author> authors) {
        return authors.stream()
                .map(author -> new AuthorExternalDto(
                        author.getId(),
                        author.getFirstName(),
                        author.getLastName()))
                .collect(Collectors.toList());
    }

    /**
     * Mapuje encję wydawcy na DTO wydawcy.
     *
     * @param publisher encja wydawcy
     * @return DTO wydawcy
     */
    private PublisherExternalDto mapPublisherToDto(Publisher publisher) {
        return new PublisherExternalDto(
                publisher.getId(),
                publisher.getName()
        );
    }
}
