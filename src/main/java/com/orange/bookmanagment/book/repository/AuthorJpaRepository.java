package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface AuthorJpaRepository extends JpaRepository<Author,Long> {

//    List<Author> findAuthorByAuthorName(String authorName);
}
