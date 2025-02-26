package com.orange.bookmanagment.book.service;

import com.orange.bookmanagment.book.model.Book;

public interface BookService {
    Book saveBook(Book book);

    Book findBookById(long id);

    Book findBookByTitle(String title);

    int updateBook(Book book);

    int deleteBookById(long id);
}
