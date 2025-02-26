package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Book;

import java.util.Optional;

public interface BookRepository {
    Book saveBook(Book book);

    Optional<Book> findBookById(long id);

    Optional<Book> findBookByTitle(String title);

    int updateBook(Book book);

    int deleteBookById(long id);
}
