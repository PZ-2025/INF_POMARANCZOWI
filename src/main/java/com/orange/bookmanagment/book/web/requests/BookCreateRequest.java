package com.orange.bookmanagment.book.web.requests;

import com.orange.bookmanagment.book.model.Publisher;

import java.util.List;

public record BookCreateRequest(
        String title,
        List<AuthorCreateRequest> authors,
        PublisherCreateRequest publisher,
        String description,
        String genre,
        String coverImage
) {
}
