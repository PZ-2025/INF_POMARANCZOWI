package com.orange.bookmanagment.book.web.requests;

import java.util.List;

public record BookUpdateRequest(
        String title,
        List<AuthorCreateRequest> authors,
        PublisherCreateRequest publisher,
        String description,
        String genre,
        String coverImage
) { }
