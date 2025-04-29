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

@Component
public class BookInternalMapper {

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

    private List<AuthorExternalDto> mapAuthorsToDto(List<Author> authors) {
        return authors.stream()
                .map(author -> new AuthorExternalDto(
                        author.getId(),
                        author.getFirstName(),
                        author.getLastName()))
                .collect(Collectors.toList());
    }

    private PublisherExternalDto mapPublisherToDto(Publisher publisher) {
        return new PublisherExternalDto(
                publisher.getId(),
                publisher.getName()
        );
    }
}
