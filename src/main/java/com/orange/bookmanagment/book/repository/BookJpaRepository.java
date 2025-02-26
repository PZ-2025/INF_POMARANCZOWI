package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface BookJpaRepository extends JpaRepository<Book,Long> {

    Optional<Book> findBookByTitle(String title);
}
