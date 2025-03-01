package com.orange.bookmanagment.book.web.request;

import com.orange.bookmanagment.book.model.Author;

import java.util.List;

public record BookCreateRequest(
        String title,
        List<AuthorCreateRequest> authors,
        String publisher,
        String description,
        String genre,
        String coverImage
) {
}
