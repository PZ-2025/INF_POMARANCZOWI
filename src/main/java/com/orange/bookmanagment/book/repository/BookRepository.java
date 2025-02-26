package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class BookRepository {

    private final BookJpaRepository bookJpaRepository;

    public Book saveBook(Book book) {
        return bookJpaRepository.save(book);
    }
    public Optional<Book> findBookById(long id){
        return bookJpaRepository.findById(id);
    }

    public Optional<Book> findBookByTitle(String title){
        return bookJpaRepository.findBookByTitle(title);
    }




}
