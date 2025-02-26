package com.orange.bookmanagment.book.service.impl;

import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl {

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

    public int updateBook(Book book) {
        return bookRepository.updateBook(book);
    }

    public int deleteBookById(long id) {
        return bookRepository.deleteBookById(id);
    }
}
