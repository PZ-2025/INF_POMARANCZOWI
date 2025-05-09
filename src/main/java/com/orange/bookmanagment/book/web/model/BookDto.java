package com.orange.bookmanagment.book.web.model;

import com.orange.bookmanagment.shared.enums.BookStatus;

import java.util.List;

public record BookDto(
        Long id,
        String title,
        List<AuthorDto> authors,
        PublisherDto publisher,
        String description,
        String genre,
        BookStatus status,
        String coverImage,
        String created_at,
        String updated_at
) {
}
