package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.shared.enums.BookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookRepositoryTest {

    @Mock
    private BookJpaRepository bookJpaRepository;

    @InjectMocks
    private BookRepository bookRepository;

    private Book testBook;
    private static final long BOOK_ID = 1L;
    private static final String BOOK_TITLE = "Test Book";

    @BeforeEach
    void setUp() {
        // Przygotowanie danych testowych
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("Jan");
        author.setLastName("Kowalski");
        author.setBiography("Polski pisarz");

        Publisher publisher = new Publisher();
        publisher.setId(1L);
        publisher.setName("Test Publisher");
        publisher.setDescription("Test Publisher Description");

        testBook = new Book(
                BOOK_TITLE,
                List.of(author),
                publisher,
                "Test Description",
                "Fiction",
                BookStatus.AVAILABLE,
                "cover.jpg"
        );
        testBook.setId(BOOK_ID);
    }

    @Test
    void saveBook_shouldDelegateToJpaRepository() {
        // given
        when(bookJpaRepository.save(testBook)).thenReturn(testBook);

        // when
        Book result = bookRepository.saveBook(testBook);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testBook);

        verify(bookJpaRepository).save(testBook);
    }

    @Test
    void findBookById_whenBookExists_shouldReturnBook() {
        // given
        when(bookJpaRepository.findById(BOOK_ID)).thenReturn(Optional.of(testBook));

        // when
        Optional<Book> result = bookRepository.findBookById(BOOK_ID);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testBook);

        verify(bookJpaRepository).findById(BOOK_ID);
    }

    @Test
    void findBookById_whenBookDoesNotExist_shouldReturnEmpty() {
        // given
        when(bookJpaRepository.findById(BOOK_ID)).thenReturn(Optional.empty());

        // when
        Optional<Book> result = bookRepository.findBookById(BOOK_ID);

        // then
        assertThat(result).isEmpty();

        verify(bookJpaRepository).findById(BOOK_ID);
    }

    @Test
    void findAllBooks_shouldDelegateToJpaRepository() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(testBook), pageable, 1);

        when(bookJpaRepository.findAll(pageable)).thenReturn(bookPage);

        // when
        Page<Book> result = bookRepository.findAllBooks(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testBook);

        verify(bookJpaRepository).findAll(pageable);
    }

    @Test
    void findAllBooks_whenNoBooks_shouldReturnEmptyPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(bookJpaRepository.findAll(pageable)).thenReturn(emptyPage);

        // when
        Page<Book> result = bookRepository.findAllBooks(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(bookJpaRepository).findAll(pageable);
    }

    @Test
    void findBookByTitle_shouldDelegateToJpaRepository() {
        // given
        when(bookJpaRepository.findBookByTitle(BOOK_TITLE)).thenReturn(List.of(testBook));

        // when
        List<Book> result = bookRepository.findBookByTitle(BOOK_TITLE);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testBook);

        verify(bookJpaRepository).findBookByTitle(BOOK_TITLE);
    }

    @Test
    void findBookByTitle_whenNoBooks_shouldReturnEmptyList() {
        // given
        when(bookJpaRepository.findBookByTitle(anyString())).thenReturn(Collections.emptyList());

        // when
        List<Book> result = bookRepository.findBookByTitle("Non-existent Title");

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(bookJpaRepository).findBookByTitle("Non-existent Title");
    }

    @Test
    void existsById_whenBookExists_shouldReturnTrue() {
        // given
        when(bookJpaRepository.existsById(BOOK_ID)).thenReturn(true);

        // when
        boolean result = bookRepository.existsById(BOOK_ID);

        // then
        assertThat(result).isTrue();

        verify(bookJpaRepository).existsById(BOOK_ID);
    }

    @Test
    void existsById_whenBookDoesNotExist_shouldReturnFalse() {
        // given
        when(bookJpaRepository.existsById(BOOK_ID)).thenReturn(false);

        // when
        boolean result = bookRepository.existsById(BOOK_ID);

        // then
        assertThat(result).isFalse();

        verify(bookJpaRepository).existsById(BOOK_ID);
    }
}