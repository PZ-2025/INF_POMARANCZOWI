package com.orange.bookmanagment.book.service.impl;

import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.enums.BookStatus;
import com.orange.bookmanagment.book.repository.BookRepository;
import com.orange.bookmanagment.book.service.AuthorService;
import com.orange.bookmanagment.book.service.BookService;
import com.orange.bookmanagment.book.web.mapper.BookDtoMapper;
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
class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final AuthorService authorService;
    private final BookDtoMapper bookDtoMapper;

    @Override
    @Transactional
    public Book createBook(BookCreateRequest bookCreateRequest) {
        List<AuthorCreateRequest> authorCreateRequests = bookCreateRequest.authors();
        List<Author> authors = authorService.createAuthors(authorCreateRequests);
        return bookRepository.saveBook(new Book(bookCreateRequest.title(), authors, bookCreateRequest.publisher(), bookCreateRequest.description(), bookCreateRequest.genre(), BookStatus.AVAILABLE, bookCreateRequest.coverImage()));
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
}
