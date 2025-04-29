package com.orange.bookmanagment.book.service.impl;

import com.orange.bookmanagment.book.api.BookExternalService;
import com.orange.bookmanagment.book.api.dto.BookExternalDto;
import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.book.service.mapper.BookInternalMapper;
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

@Service
@RequiredArgsConstructor
class BookServiceImpl implements BookService, BookExternalService {

    private final BookRepository bookRepository;

    private final AuthorService authorService;
    private final BookInternalMapper bookInternalMapper;
    private final PublisherService publisherService;

    @Override
    @Transactional
    public Book createBook(BookCreateRequest bookCreateRequest) {
        List<AuthorCreateRequest> authorCreateRequests = bookCreateRequest.authors();
        List<Author> authors = authorService.createAuthors(authorCreateRequests);

        Publisher publisher = publisherService.createPublisher(bookCreateRequest.publisher());

        return bookRepository.saveBook(new Book(bookCreateRequest.title(), authors, publisher, bookCreateRequest.description(), bookCreateRequest.genre(), BookStatus.AVAILABLE, bookCreateRequest.coverImage()));
    }

    @Override
    public Book getBookById(long id) throws BookNotFoundException {
        return bookRepository.findBookById(id).orElseThrow(() -> new BookNotFoundException("Book not found by id"));
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAllBooks(pageable);
    }

    @Override
    public List<Book> getBookByTitle(String title) {
        return bookRepository.findBookByTitle(title);
    }

    @Override
    public Book updateBook(Book book) throws BookNotFoundException {
        bookRepository.findBookById(book.getId()).orElseThrow(() -> new BookNotFoundException("Book not found by id"));

        return bookRepository.saveBook(book);
    }

    @Override
    public boolean existsById(long id) {
        return bookRepository.existsById(id);
    }

    //getBookStatusById
    @Override
    public BookStatus getBookStatusById(long id) {
        return bookRepository.findBookById(id).orElseThrow(() -> new BookNotFoundException("Book not found by id")).getStatus();
    }

    @Override
    @Transactional
    public void updateBookStatus(long id, BookStatus status) {
        Book book = bookRepository.findBookById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found by id"));
        book.setStatus(status);
        bookRepository.saveBook(book);
    }

    @Override
    public BookExternalDto getBookForExternal(long id) {
        Book book = this.getBookById(id);
        return bookInternalMapper.toDto(book);
    }

    @Override
    public BookStatus getBookStatusForExternal(long id) {
        return this.getBookStatusById(id);
    }

    @Override
    public boolean existsBookById(long id) {
        return this.existsById(id);
    }
}
