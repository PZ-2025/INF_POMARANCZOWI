package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuthorRepository {

    private final AuthorJpaRepository authorJpaRepository;

    public List<Author> saveAllAuthors(List<Author> authors) {
        return authorJpaRepository.saveAll(authors);
    }
}
