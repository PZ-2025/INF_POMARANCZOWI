package com.orange.bookmanagment.book.web.mapper;

import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.web.model.AuthorDto;
import com.orange.bookmanagment.book.web.requests.AuthorCreateRequest;
import org.springframework.stereotype.Component;

/**
 * Komponent odpowiedzialny za mapowanie między encją {@link Author} a jej reprezentacją DTO.
 * Umożliwia konwersję do i z modelu API.
 */
@Component
public class AuthorMapper {

    /**
     * Konwertuje żądanie utworzenia autora na encję.
     *
     * @param authorCreateRequest dane autora z żądania
     * @return utworzona encja autora
     */
    public Author toEntity(AuthorCreateRequest authorCreateRequest) {
        return new Author(
                authorCreateRequest.firstName(),
                authorCreateRequest.lastName(),
                authorCreateRequest.biography()
        );
    }

    /**
     * Konwertuje encję autora na obiekt DTO.
     *
     * @param author encja autora
     * @return DTO autora
     */
    public AuthorDto toDto(Author author) {
        return new AuthorDto(
                author.getFirstName(),
                author.getLastName(),
                author.getBiography()
        );
    }
}
