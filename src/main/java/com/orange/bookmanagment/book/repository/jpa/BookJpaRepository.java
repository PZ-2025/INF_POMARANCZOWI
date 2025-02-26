package com.orange.bookmanagment.book.repository.jpa;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.repository.BookRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookJpaRepository extends JpaRepository<Book,Long>, BookRepository {
}
