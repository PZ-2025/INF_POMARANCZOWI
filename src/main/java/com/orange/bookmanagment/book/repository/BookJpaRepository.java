package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface BookJpaRepository extends JpaRepository<Book,Long> {

    List<Book> findBookByTitle(String title);
}
