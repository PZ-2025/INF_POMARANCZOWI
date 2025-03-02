package com.orange.bookmanagment.book.service;

import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.web.request.BookCreateRequest;

import java.util.List;

public interface BookService {
    Book createBook(BookCreateRequest bookCreateRequest);

    Book getBookById(long id) throws BookNotFoundException;

    List<Book> getAllBooks();

    List<Book> getBookByTitle(String title);

    Book updateBook(Book book) throws BookNotFoundException;
}
