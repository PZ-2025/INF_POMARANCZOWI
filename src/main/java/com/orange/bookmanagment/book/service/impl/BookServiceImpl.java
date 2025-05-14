package com.orange.bookmanagment.book.service.impl;

import com.orange.bookmanagment.book.api.BookExternalService;
import com.orange.bookmanagment.book.api.dto.BookExternalDto;
import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.book.service.mapper.BookInternalMapper;
import com.orange.bookmanagment.book.web.mapper.BookDtoMapper;
import com.orange.bookmanagment.book.web.model.BookDto;
import com.orange.bookmanagment.shared.enums.BookStatus;
import com.orange.bookmanagment.book.repository.BookRepository;
import com.orange.bookmanagment.book.service.AuthorService;
import com.orange.bookmanagment.book.service.BookService;
import com.orange.bookmanagment.book.service.PublisherService;
import com.orange.bookmanagment.book.web.requests.AuthorCreateRequest;
import com.orange.bookmanagment.book.web.requests.BookCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementacja serwisu {@link BookService} oraz {@link BookExternalService}.
 * <p>
 * Odpowiada za operacje na książkach, takie jak tworzenie, aktualizacja,
 * pobieranie, wyszukiwanie, losowanie oraz integrację z API zewnętrznym.
 */
@Service
@RequiredArgsConstructor
class BookServiceImpl implements BookService, BookExternalService {

    private final BookRepository bookRepository;
    private final BookDtoMapper bookDtoMapper;
    private final AuthorService authorService;
    private final BookInternalMapper bookInternalMapper;
    private final PublisherService publisherService;

    /**
     * Tworzy nową książkę na podstawie żądania.
     *
     * @param bookCreateRequest dane nowej książki
     * @return utworzona encja książki
     */
    @Override
    @Transactional
    public Book createBook(BookCreateRequest bookCreateRequest) {
        List<AuthorCreateRequest> authorCreateRequests = bookCreateRequest.authors();
        List<Author> authors = authorService.createAuthors(authorCreateRequests);

        Publisher publisher = publisherService.createPublisher(bookCreateRequest.publisher());

        return bookRepository.saveBook(new Book(bookCreateRequest.title(), authors, publisher, bookCreateRequest.description(), bookCreateRequest.genre(), BookStatus.AVAILABLE, bookCreateRequest.coverImage()));
    }

    /**
     * Zwraca książkę o podanym ID lub rzuca wyjątek, jeśli nie istnieje.
     *
     * @param id identyfikator książki
     * @return encja książki
     * @throws BookNotFoundException jeśli książka nie została znaleziona
     */
    @Override
    public Book getBookById(long id) throws BookNotFoundException {
        return bookRepository.findBookById(id).orElseThrow(() -> new BookNotFoundException("Book not found by id"));
    }

    /**
     * Zwraca wszystkie książki w formie stronicowanej.
     *
     * @param pageable dane dotyczące paginacji
     * @return strona książek
     */
    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAllBooks(pageable);
    }

    /**
     * Wyszukuje książki po tytule.
     *
     * @param title tytuł książki
     * @return lista dopasowanych książek
     */
    @Override
    public List<Book> getBookByTitle(String title) {
        return bookRepository.findBookByTitle(title);
    }

    /**
     * Aktualizuje książkę, jeśli istnieje.
     *
     * @param book zaktualizowana encja książki
     * @return zapisany obiekt książki
     * @throws BookNotFoundException jeśli książka nie istnieje
     */
    @Override
    public Book updateBook(Book book) throws BookNotFoundException {
        bookRepository.findBookById(book.getId()).orElseThrow(() -> new BookNotFoundException("Book not found by id"));

        return bookRepository.saveBook(book);
    }

    /**
     * Sprawdza, czy książka o danym ID istnieje.
     *
     * @param id identyfikator książki
     * @return true, jeśli istnieje; false w przeciwnym razie
     */
    @Override
    public boolean existsById(long id) {
        return bookRepository.existsById(id);
    }

    /**
     * Zwraca status książki na podstawie ID.
     *
     * @param id identyfikator książki
     * @return status książki
     * @throws BookNotFoundException jeśli książka nie istnieje
     */
    @Override
    public BookStatus getBookStatusById(long id) {
        return bookRepository.findBookById(id).orElseThrow(() -> new BookNotFoundException("Book not found by id")).getStatus();
    }

    /**
     * Aktualizuje status książki i zapisuje zmiany.
     *
     * @param id identyfikator książki
     * @param status nowy status książki
     * @throws BookNotFoundException jeśli książka nie istnieje
     */
    @Override
    @Transactional
    public void updateBookStatus(long id, BookStatus status) {
        Book book = bookRepository.findBookById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found by id"));
        book.setStatus(status);
        bookRepository.saveBook(book);
    }

    /**
     * Zwraca dane książki dla systemów zewnętrznych.
     *
     * @param id identyfikator książki
     * @return DTO książki
     */
    @Override
    public BookExternalDto getBookForExternal(long id) {
        Book book = this.getBookById(id);
        return bookInternalMapper.toDto(book);
    }

    /**
     * Zwraca status książki dla systemów zewnętrznych.
     *
     * @param id identyfikator książki
     * @return status książki
     */
    @Override
    public BookStatus getBookStatusForExternal(long id) {
        return this.getBookStatusById(id);
    }

    /**
     * Sprawdza istnienie książki na potrzeby API zewnętrznego.
     *
     * @param id identyfikator książki
     * @return true, jeśli książka istnieje; false w przeciwnym razie
     */
    @Override
    public boolean existsBookById(long id) {
        return this.existsById(id);
    }

    /**
     * Zwraca wszystkie książki jako DTO dla API zewnętrznego.
     *
     * @return lista DTO książek
     */
    @Override
    public List<BookExternalDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(bookInternalMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Zwraca książki o podanym statusie jako DTO dla API zewnętrznego.
     *
     * @param status status książki
     * @return lista książek w formie DTO
     */
    @Override
    public List<BookExternalDto> getBooksByStatus(BookStatus status) {
        List<Book> books = bookRepository.findByStatus(status);
        return books.stream()
                .map(bookInternalMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Zwraca losowe książki w formie DTO do prezentacji w aplikacji.
     *
     * @param limit maksymalna liczba książek
     * @return lista książek w formie DTO
     */
    @Override
    public List<BookDto> getRandomBooks(int limit) {
        List<Book> books = bookRepository.findRandomBooks(limit);
        return books.stream().map(bookDtoMapper::toDto).toList();
    }

    /**
     * Zwraca losowe książki dla danego gatunku.
     *
     * @param genre gatunek
     * @param limit maksymalna liczba książek
     * @return lista książek w formie DTO
     */
    @Override
    public List<BookDto> getRandomBooksByGenre(String genre, int limit) {
        List<Book> books = bookRepository.findRandomBooksByGenre(genre, limit);
        return books.stream().map(bookDtoMapper::toDto).toList();
    }

    /**
     * Zwraca 5 najpopularniejszych gatunków książek.
     *
     * @return lista nazw gatunków
     */
    @Override
    public List<String> getTop5Genres() {
        return bookRepository.findTop5Genres();
    }

    /**
     * Wyszukuje książki na podstawie zapytania tekstowego.
     *
     * @param query zapytanie tekstowe (tytuł, autor, wydawca, gatunek)
     * @return lista dopasowanych książek
     */
    @Override
    public List<Book> searchBooks(String query) {
        return bookRepository.searchBooks(query.toLowerCase());
    }
}
