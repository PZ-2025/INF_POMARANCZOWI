package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Author;
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
class AuthorRepositoryTest {

    @Mock
    private AuthorJpaRepository authorJpaRepository;

    @InjectMocks
    private AuthorRepository authorRepository;

    private List<Author> authors;

    @BeforeEach
    void setUp() {
        // Przygotowanie danych testowych
        Author author1 = new Author();
        author1.setId(1L);
        author1.setFirstName("Jan");
        author1.setLastName("Kowalski");
        author1.setBiography("Polski pisarz");

        Author author2 = new Author();
        author2.setId(2L);
        author2.setFirstName("Anna");
        author2.setLastName("Nowak");
        author2.setBiography("Polska pisarka");

        authors = Arrays.asList(author1, author2);
    }

    @Test
    void saveAllAuthors_shouldDelegateToJpaRepository() {
        // given
        when(authorJpaRepository.saveAll(authors)).thenReturn(authors);

        // when
        List<Author> result = authorRepository.saveAllAuthors(authors);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(authors);

        verify(authorJpaRepository).saveAll(authors);
    }

    @Test
    void saveAllAuthors_whenEmptyList_shouldReturnEmptyList() {
        // given
        List<Author> emptyList = Collections.emptyList();
        when(authorJpaRepository.saveAll(emptyList)).thenReturn(emptyList);

        // when
        List<Author> result = authorRepository.saveAllAuthors(emptyList);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(authorJpaRepository).saveAll(emptyList);
    }
}