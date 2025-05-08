package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * <p>Repository class for managing {@link Book} entities.</p>
 * It uses Spring Data JPA to perform CRUD operations and custom queries.
 * The class is annotated with @Repository to indicate that it is a Spring Data repository.
 * The class is also annotated with @RequiredArgsConstructor to generate a constructor with required arguments.
 */
@Repository
@RequiredArgsConstructor
public class BookRepository {

    private final BookJpaRepository bookJpaRepository;

    /**
     * Saves a book entity to the database.
     *
     * @param book the book entity to save
     * @return the saved book entity
     */
    public Book saveBook(Book book) {
        return bookJpaRepository.save(book);
    }

    /**
     * Finds a book entity by its ID.
     *
     * @param id the ID of the book entity to find
     * @return an {@link Optional} containing the found book entity, or empty if not found
     */
    public Optional<Book> findBookById(long id){
        return bookJpaRepository.findById(id);
    }

    /**
     * Retrieves all book entities with pagination support.
     *
     * @param pageable the pagination information
     * @return a {@link Page} containing the book entities
     */
    public Page<Book> findAllBooks(Pageable pageable){
        return bookJpaRepository.findAll(pageable);
    }

    /**
     * Finds book entities by their title.
     *
     * @param title the title of the books to find
     * @return a list of book entities with the given title
     */
    public List<Book> findBookByTitle(String title){
        return bookJpaRepository.findBookByTitle(title);
    }

    //existsById
    /**
     * Checks if a book entity exists by its ID.
     *
     * @param id the ID of the book entity to check
     * @return true if the book entity exists, false otherwise
     */
    public boolean existsById(long id) {
        return bookJpaRepository.existsById(id);
    }


}
