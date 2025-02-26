package com.orange.bookmanagment.book.service;

import com.orange.bookmanagment.book.exception.BookAlreadyExistException;
import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Book;

import java.util.List;

public interface BookService {
    Book addBook(Book book) throws BookAlreadyExistException;

    Book getBookById(long id) throws BookNotFoundException;

    List<Book> getBookByTitle(String title);

    Book updateBook(Book book) throws BookNotFoundException;
}
