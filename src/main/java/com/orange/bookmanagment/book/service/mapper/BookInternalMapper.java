package com.orange.bookmanagment.book.service.mapper;

import com.orange.bookmanagment.book.api.dto.AuthorInternalDto;
import com.orange.bookmanagment.book.api.BookInternalDto;
import com.orange.bookmanagment.book.api.dto.PublisherInternalDto;
import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.Publisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookInternalMapper {

    public BookInternalDto toDto(Book book) {
        if (book == null) {
            return null;
        }

        return new BookInternalDto(
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

    private List<AuthorInternalDto> mapAuthorsToDto(List<Author> authors) {
        return authors.stream()
                .map(author -> new AuthorInternalDto(
                        author.getId(),
                        author.getFirstName(),
                        author.getLastName()))
                .collect(Collectors.toList());
    }

    private PublisherInternalDto mapPublisherToDto(Publisher publisher) {
        return new PublisherInternalDto(
                publisher.getId(),
                publisher.getName()
        );
    }
}
