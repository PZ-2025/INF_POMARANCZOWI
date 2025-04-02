package com.orange.bookmanagment.book.service;

import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.web.requests.AuthorCreateRequest;

import java.util.List;

/**
 * <p>Service interface for managing authors.</p>
 *
 */
public interface AuthorService {

    /**
     * <p>Creates a list of authors based on the provided requests.</p>
     *
     * @param authorCreateRequests list of requests to create authors
     * @return list of created authors
     */
    List<Author> createAuthors(List<AuthorCreateRequest> authorCreateRequests);
}
