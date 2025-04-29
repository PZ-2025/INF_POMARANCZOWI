package com.orange.bookmanagment.book.service.impl;

import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.repository.AuthorRepository;
import com.orange.bookmanagment.book.web.mapper.AuthorMapper;
import com.orange.bookmanagment.book.web.requests.AuthorCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private AuthorCreateRequest authorRequest1;
    private AuthorCreateRequest authorRequest2;
    private Author author1;
    private Author author2;

    @BeforeEach
    void setUp() {
        // Przygotowanie testowych obiektów
        authorRequest1 = new AuthorCreateRequest("Jan", "Kowalski", "Polski pisarz");
        authorRequest2 = new AuthorCreateRequest("Anna", "Nowak", "Polska pisarka");

        author1 = new Author();
        author1.setId(1L);
        author1.setFirstName("Jan");
        author1.setLastName("Kowalski");
        author1.setBiography("Polski pisarz");

        author2 = new Author();
        author2.setId(2L);
        author2.setFirstName("Anna");
        author2.setLastName("Nowak");
        author2.setBiography("Polska pisarka");
    }

    @Test
    void createAuthors_shouldMapAndSaveAuthors() {
        // given
        List<AuthorCreateRequest> requests = Arrays.asList(authorRequest1, authorRequest2);
        List<Author> mappedAuthors = Arrays.asList(author1, author2);

        when(authorMapper.toEntity(authorRequest1)).thenReturn(author1);
        when(authorMapper.toEntity(authorRequest2)).thenReturn(author2);
        when(authorRepository.saveAllAuthors(anyList())).thenReturn(mappedAuthors);

        // when
        List<Author> result = authorService.createAuthors(requests);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(author1, author2);

        verify(authorMapper).toEntity(authorRequest1);
        verify(authorMapper).toEntity(authorRequest2);
        verify(authorRepository).saveAllAuthors(mappedAuthors);
    }

    @Test
    void createAuthors_whenEmptyList_shouldReturnEmptyList() {
        // given
        List<AuthorCreateRequest> requests = Collections.emptyList();
        List<Author> mappedAuthors = Collections.emptyList();

        when(authorRepository.saveAllAuthors(anyList())).thenReturn(mappedAuthors);

        // when
        List<Author> result = authorService.createAuthors(requests);

        // then
        assertThat(result).isEmpty();
        verify(authorRepository).saveAllAuthors(mappedAuthors);
    }

    @Test
    void createAuthors_withSingleAuthor_shouldCreateAuthor() {
        // given
        List<AuthorCreateRequest> requests = List.of(authorRequest1);
        List<Author> mappedAuthors = List.of(author1);

        when(authorMapper.toEntity(authorRequest1)).thenReturn(author1);
        when(authorRepository.saveAllAuthors(anyList())).thenReturn(mappedAuthors);

        // when
        List<Author> result = authorService.createAuthors(requests);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(author1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Jan");
        assertThat(result.get(0).getLastName()).isEqualTo("Kowalski");

        verify(authorMapper).toEntity(authorRequest1);
        verify(authorRepository).saveAllAuthors(mappedAuthors);
    }
}