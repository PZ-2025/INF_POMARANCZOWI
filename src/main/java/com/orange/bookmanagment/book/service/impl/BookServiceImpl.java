package com.orange.bookmanagment.book.service.impl;

import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.repository.BookRepository;
import com.orange.bookmanagment.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public Book saveBook(Book book) {
        return bookRepository.saveBook(book);
    }

    public Book findBookById(long id) {
        return bookRepository.findBookById(id).orElseThrow(() -> new BookNotFoundException("Book not found by id"));
    }

    public Book findBookByTitle(String title) {
        return bookRepository.findBookByTitle(title).orElseThrow(() -> new BookNotFoundException("Book not found by title"));
    }

    public Book updateBook(Book book) {
        return bookRepository.saveBook(book);
    }
}
