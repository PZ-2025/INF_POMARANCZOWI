package com.orange.bookmanagment.book.service.impl;

import com.orange.bookmanagment.book.api.dto.BookExternalDto;
import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.book.repository.BookRepository;
import com.orange.bookmanagment.book.service.AuthorService;
import com.orange.bookmanagment.book.service.PublisherService;
import com.orange.bookmanagment.book.service.mapper.BookInternalMapper;
import com.orange.bookmanagment.book.web.requests.AuthorCreateRequest;
import com.orange.bookmanagment.book.web.requests.BookCreateRequest;
import com.orange.bookmanagment.book.web.requests.PublisherCreateRequest;
import com.orange.bookmanagment.shared.enums.BookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorService authorService;

    @Mock
    private PublisherService publisherService;

    @Mock
    private BookInternalMapper bookInternalMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    private Book testBook;
    private Publisher testPublisher;
    private List<Author> testAuthors;
    private BookCreateRequest bookCreateRequest;
    private PublisherCreateRequest publisherCreateRequest;
    private List<AuthorCreateRequest> authorCreateRequests;
    private BookExternalDto bookExternalDto;

    private static final long BOOK_ID = 1L;
    private static final String BOOK_TITLE = "Test Book";
    private static final String BOOK_DESCRIPTION = "Test Description";
    private static final String BOOK_GENRE = "Fiction";
    private static final String BOOK_COVER_IMAGE = "cover.jpg";

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

        testAuthors = Arrays.asList(author1, author2);

        testPublisher = new Publisher();
        testPublisher.setId(1L);
        testPublisher.setName("Test Publisher");
        testPublisher.setDescription("Test Publisher Description");

        testBook = new Book(
                BOOK_TITLE,
                testAuthors,
                testPublisher,
                BOOK_DESCRIPTION,
                BOOK_GENRE,
                BookStatus.AVAILABLE,
                BOOK_COVER_IMAGE
        );
        testBook.setId(BOOK_ID);

        // Request objects
        authorCreateRequests = Arrays.asList(
                new AuthorCreateRequest("Jan", "Kowalski", "Polski pisarz"),
                new AuthorCreateRequest("Anna", "Nowak", "Polska pisarka")
        );

        publisherCreateRequest = new PublisherCreateRequest("Test Publisher", "Test Publisher Description");

        bookCreateRequest = new BookCreateRequest(
                BOOK_TITLE,
                authorCreateRequests,
                publisherCreateRequest,
                BOOK_DESCRIPTION,
                BOOK_GENRE,
                BOOK_COVER_IMAGE
        );

        // DTO for external service
        bookExternalDto = new BookExternalDto(
                BOOK_ID,
                BOOK_TITLE,
                null,
                null,
                BOOK_DESCRIPTION,
                BOOK_GENRE,
                BookStatus.AVAILABLE,
                BOOK_COVER_IMAGE,
                null,
                null
        );
    }

    @Test
    void createBook_shouldCreateAndSaveBook() {
        // given
        when(authorService.createAuthors(authorCreateRequests)).thenReturn(testAuthors);
        when(publisherService.createPublisher(publisherCreateRequest)).thenReturn(testPublisher);
        when(bookRepository.saveBook(any(Book.class))).thenReturn(testBook);

        // when
        Book result = bookService.createBook(bookCreateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(BOOK_TITLE);
        assertThat(result.getAuthors()).isEqualTo(testAuthors);
        assertThat(result.getPublisher()).isEqualTo(testPublisher);
        assertThat(result.getStatus()).isEqualTo(BookStatus.AVAILABLE);

        verify(authorService).createAuthors(authorCreateRequests);
        verify(publisherService).createPublisher(publisherCreateRequest);
        verify(bookRepository).saveBook(bookCaptor.capture());

        Book capturedBook = bookCaptor.getValue();
        assertThat(capturedBook.getTitle()).isEqualTo(BOOK_TITLE);
        assertThat(capturedBook.getAuthors()).isEqualTo(testAuthors);
        assertThat(capturedBook.getPublisher()).isEqualTo(testPublisher);
        assertThat(capturedBook.getDescription()).isEqualTo(BOOK_DESCRIPTION);
        assertThat(capturedBook.getGenre()).isEqualTo(BOOK_GENRE);
        assertThat(capturedBook.getStatus()).isEqualTo(BookStatus.AVAILABLE);
        assertThat(capturedBook.getCoverImage()).isEqualTo(BOOK_COVER_IMAGE);
    }

    @Test
    void getBookById_whenBookExists_shouldReturnBook() {
        // given
        when(bookRepository.findBookById(BOOK_ID)).thenReturn(Optional.of(testBook));

        // when
        Book result = bookService.getBookById(BOOK_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(BOOK_ID);
        assertThat(result.getTitle()).isEqualTo(BOOK_TITLE);

        verify(bookRepository).findBookById(BOOK_ID);
    }

    @Test
    void getBookById_whenBookDoesNotExist_shouldThrowException() {
        // given
        when(bookRepository.findBookById(BOOK_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> bookService.getBookById(BOOK_ID))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found by id");

        verify(bookRepository).findBookById(BOOK_ID);
    }

    @Test
    void getAllBooks_shouldReturnPagedBooks() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(testBook), pageable, 1);
        when(bookRepository.findAllBooks(pageable)).thenReturn(bookPage);

        // when
        Page<Book> result = bookService.getAllBooks(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo(BOOK_TITLE);

        verify(bookRepository).findAllBooks(pageable);
    }

    @Test
    void getBookByTitle_shouldReturnMatchingBooks() {
        // given
        when(bookRepository.findBookByTitle(BOOK_TITLE)).thenReturn(List.of(testBook));

        // when
        List<Book> result = bookService.getBookByTitle(BOOK_TITLE);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(BOOK_TITLE);

        verify(bookRepository).findBookByTitle(BOOK_TITLE);
    }

    @Test
    void getBookByTitle_whenNoMatch_shouldReturnEmptyList() {
        // given
        when(bookRepository.findBookByTitle(anyString())).thenReturn(List.of());

        // when
        List<Book> result = bookService.getBookByTitle("Non-existent title");

        // then
        assertThat(result).isEmpty();

        verify(bookRepository).findBookByTitle("Non-existent title");
    }

    @Test
    void updateBook_whenBookExists_shouldUpdateBook() {
        // given
        Book updatedBook = new Book(
                "Updated Title",
                testAuthors,
                testPublisher,
                "Updated Description",
                "Updated Genre",
                BookStatus.AVAILABLE,
                "updated-cover.jpg"
        );
        updatedBook.setId(BOOK_ID);

        when(bookRepository.findBookById(BOOK_ID)).thenReturn(Optional.of(testBook));
        when(bookRepository.saveBook(updatedBook)).thenReturn(updatedBook);

        // when
        Book result = bookService.updateBook(updatedBook);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("Updated Description");

        verify(bookRepository).findBookById(BOOK_ID);
        verify(bookRepository).saveBook(updatedBook);
    }

    @Test
    void updateBook_whenBookDoesNotExist_shouldThrowException() {
        // given
        Book updatedBook = new Book(
                "Updated Title",
                testAuthors,
                testPublisher,
                "Updated Description",
                "Updated Genre",
                BookStatus.AVAILABLE,
                "updated-cover.jpg"
        );
        updatedBook.setId(BOOK_ID);

        when(bookRepository.findBookById(BOOK_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> bookService.updateBook(updatedBook))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found by id");

        verify(bookRepository).findBookById(BOOK_ID);
        verify(bookRepository, never()).saveBook(any(Book.class));
    }

    @Test
    void existsById_whenBookExists_shouldReturnTrue() {
        // given
        when(bookRepository.existsById(BOOK_ID)).thenReturn(true);

        // when
        boolean result = bookService.existsById(BOOK_ID);

        // then
        assertThat(result).isTrue();
        verify(bookRepository).existsById(BOOK_ID);
    }

    @Test
    void existsById_whenBookDoesNotExist_shouldReturnFalse() {
        // given
        when(bookRepository.existsById(BOOK_ID)).thenReturn(false);

        // when
        boolean result = bookService.existsById(BOOK_ID);

        // then
        assertThat(result).isFalse();
        verify(bookRepository).existsById(BOOK_ID);
    }

    @Test
    void getBookStatusById_whenBookExists_shouldReturnStatus() {
        // given
        when(bookRepository.findBookById(BOOK_ID)).thenReturn(Optional.of(testBook));

        // when
        BookStatus result = bookService.getBookStatusById(BOOK_ID);

        // then
        assertThat(result).isEqualTo(BookStatus.AVAILABLE);
        verify(bookRepository).findBookById(BOOK_ID);
    }

    @Test
    void getBookStatusById_whenBookDoesNotExist_shouldThrowException() {
        // given
        when(bookRepository.findBookById(BOOK_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> bookService.getBookStatusById(BOOK_ID))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found by id");

        verify(bookRepository).findBookById(BOOK_ID);
    }

    @Test
    void updateBookStatus_whenBookExists_shouldUpdateStatus() {
        // given
        when(bookRepository.findBookById(BOOK_ID)).thenReturn(Optional.of(testBook));
        when(bookRepository.saveBook(any(Book.class))).thenReturn(testBook);

        // when
        bookService.updateBookStatus(BOOK_ID, BookStatus.BORROWED);

        // then
        verify(bookRepository).findBookById(BOOK_ID);
        verify(bookRepository).saveBook(bookCaptor.capture());

        Book capturedBook = bookCaptor.getValue();
        assertThat(capturedBook.getStatus()).isEqualTo(BookStatus.BORROWED);
    }

    @Test
    void updateBookStatus_whenBookDoesNotExist_shouldThrowException() {
        // given
        when(bookRepository.findBookById(BOOK_ID)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> bookService.updateBookStatus(BOOK_ID, BookStatus.BORROWED))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found by id");

        verify(bookRepository).findBookById(BOOK_ID);
        verify(bookRepository, never()).saveBook(any(Book.class));
    }

    @Test
    void getBookForExternal_shouldReturnMappedDto() {
        // given
        when(bookRepository.findBookById(BOOK_ID)).thenReturn(Optional.of(testBook));
        when(bookInternalMapper.toDto(testBook)).thenReturn(bookExternalDto);

        // when
        BookExternalDto result = bookService.getBookForExternal(BOOK_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(BOOK_ID);
        assertThat(result.title()).isEqualTo(BOOK_TITLE);
        assertThat(result.status()).isEqualTo(BookStatus.AVAILABLE);

        verify(bookRepository).findBookById(BOOK_ID);
        verify(bookInternalMapper).toDto(testBook);
    }

    @Test
    void getBookStatusForExternal_shouldReturnStatus() {
        // given
        when(bookRepository.findBookById(BOOK_ID)).thenReturn(Optional.of(testBook));

        // when
        BookStatus result = bookService.getBookStatusForExternal(BOOK_ID);

        // then
        assertThat(result).isEqualTo(BookStatus.AVAILABLE);
        verify(bookRepository).findBookById(BOOK_ID);
    }

    @Test
    void existsBookById_shouldDelegateToExistsById() {
        // given
        when(bookRepository.existsById(BOOK_ID)).thenReturn(true);

        // when
        boolean result = bookService.existsBookById(BOOK_ID);

        // then
        assertThat(result).isTrue();
        verify(bookRepository).existsById(BOOK_ID);
    }
}