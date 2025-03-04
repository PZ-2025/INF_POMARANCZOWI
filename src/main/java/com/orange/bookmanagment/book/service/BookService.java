package com.orange.bookmanagment.book.service;

import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.web.request.BookCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    /**
     * Creates a new book.
     *
     * @param bookCreateRequest the request containing book details
     * @return the created book
     */
    Book createBook(BookCreateRequest bookCreateRequest);

    /**
     * Retrieves a book by its ID.
     *
     * @param id the ID of the book
     * @return the book with the specified ID
     * @throws BookNotFoundException if the book is not found
     */
    Book getBookById(long id) throws BookNotFoundException;

    /**
     * Retrieves all books with pagination.
     *
     * @param pageable the pagination information
     * @return a page of books
     */
    Page<Book> getAllBooks(Pageable pageable);

    List<Book> getBookByTitle(String title);

    Book updateBook(Book book) throws BookNotFoundException;
}
