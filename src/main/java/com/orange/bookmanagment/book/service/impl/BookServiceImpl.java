package com.orange.bookmanagment.book.service.impl;

import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.enums.BookStatus;
import com.orange.bookmanagment.book.repository.BookRepository;
import com.orange.bookmanagment.book.service.AuthorService;
import com.orange.bookmanagment.book.service.BookService;
import com.orange.bookmanagment.book.web.mapper.BookDtoMapper;
import com.orange.bookmanagment.book.web.model.BookDto;
import com.orange.bookmanagment.book.web.request.AuthorCreateRequest;
import com.orange.bookmanagment.book.web.request.BookCreateRequest;
import lombok.RequiredArgsConstructor;
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
    public List<Book> getAllBooks() {
        return bookRepository.findAllBooks();
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
