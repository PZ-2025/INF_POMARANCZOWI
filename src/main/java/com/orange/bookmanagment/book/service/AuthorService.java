package com.orange.bookmanagment.book.service;

import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.web.requests.AuthorCreateRequest;

import java.util.List;

public interface AuthorService {
    List<Author> createAuthors(List<AuthorCreateRequest> authorCreateRequests);
}
