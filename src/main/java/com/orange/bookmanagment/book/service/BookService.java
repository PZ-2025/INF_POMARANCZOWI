package com.orange.bookmanagment.book.service;

import com.orange.bookmanagment.book.exception.BookNotFoundException;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.web.requests.BookCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing books.
 */
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

    /**
     * Retrieves books by their title.
     *
     * @param title the title of the book
     * @return a list of books with the specified title
     */
    List<Book> getBookByTitle(String title);

    /**
     * Updates an existing book.
     *
     * @param book the book to update
     * @return the updated book
     * @throws BookNotFoundException if the book is not found
     */
    Book updateBook(Book book) throws BookNotFoundException;
}
