package com.orange.bookmanagment.book.web.mapper;

import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.web.model.AuthorDto;
import com.orange.bookmanagment.book.web.requests.AuthorCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {
    public Author toEntity(AuthorCreateRequest authorCreateRequest) {
        return new Author(
                authorCreateRequest.firstName(),
                authorCreateRequest.lastName(),
                authorCreateRequest.biography()
        );
    }

    public AuthorDto toDto(Author author) {
        return new AuthorDto(
                author.getFirstName(),
                author.getLastName(),
                author.getBiography()
        );
    }
}
