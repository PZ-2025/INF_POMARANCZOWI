package com.orange.bookmanagment.book.service.impl;

import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.repository.AuthorRepository;
import com.orange.bookmanagment.book.service.AuthorService;
import com.orange.bookmanagment.book.web.request.AuthorCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public List<Author> createAuthors(List<AuthorCreateRequest> authorCreateRequests) {
        //TODO mapper DTO -> Entity
        List<Author> authors = authorCreateRequests.stream()
                .map(authorCreateRequest -> new Author(authorCreateRequest.firstName(), authorCreateRequest.lastName(), authorCreateRequest.biography()))
                .collect(Collectors.toList());

        return authorRepository.saveAllAuthors(authors);
    }
}
