package com.orange.bookmanagment.book.web.requests;

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
