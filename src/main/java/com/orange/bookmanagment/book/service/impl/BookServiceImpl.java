package com.orange.bookmanagment.book.service.impl;

import com.orange.bookmanagment.book.exception.BookAlreadyExistException;
import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.repository.BookRepository;
import com.orange.bookmanagment.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public Book addBook(Book book) throws BookAlreadyExistException {
        bookRepository.findBookById(book.getId()).ifPresent(b -> {
            throw new BookAlreadyExistException("Book already exists");
        });

        return bookRepository.saveBook(book);
    }

    public Book getBookById(long id) throws BookNotFoundException {
        return bookRepository.findBookById(id).orElseThrow(() -> new BookNotFoundException("Book not found by id"));
    }

    public List<Book> getBookByTitle(String title) {
        return bookRepository.findBookByTitle(title);
    }

    public Book updateBook(Book book) throws BookNotFoundException {
        bookRepository.findBookById(book.getId()).orElseThrow(() -> new BookNotFoundException("Book not found by id"));

        return bookRepository.saveBook(book);
    }
}
