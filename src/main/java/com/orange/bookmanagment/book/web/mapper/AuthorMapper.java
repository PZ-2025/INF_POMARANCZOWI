package com.orange.bookmanagment.book.web.mapper;

import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.web.model.AuthorDto;
import com.orange.bookmanagment.book.web.requests.AuthorCreateRequest;
import org.springframework.stereotype.Component;

/**
 * AuthorMapper is a component that provides methods to convert between Author entities and their corresponding DTOs.
 * It is used to map data between the database model and the API representation.
 */
@Component
public class AuthorMapper {
    /**
     * Converts an AuthorCreateRequest object to an Author entity.
     *
     * @param authorCreateRequest the AuthorCreateRequest object to convert
     * @return the converted Author entity
     */
    public Author toEntity(AuthorCreateRequest authorCreateRequest) {
        return new Author(
                authorCreateRequest.firstName(),
                authorCreateRequest.lastName(),
                authorCreateRequest.biography()
        );
    }

    /**
     * Converts an Author entity to an AuthorDto object.
     *
     * @param author the Author entity to convert
     * @return the converted AuthorDto object
     */
    public AuthorDto toDto(Author author) {
        return new AuthorDto(
                author.getFirstName(),
                author.getLastName(),
                author.getBiography()
        );
    }
}
