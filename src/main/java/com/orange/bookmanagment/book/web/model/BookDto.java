package com.orange.bookmanagment.book.web.model;

import com.orange.bookmanagment.book.model.enums.BookStatus;

import java.util.List;

public record BookDto(
        String title,
        List<AuthorDto> authors,
        String publisher,
        String description,
        String genre,
        BookStatus status,
        String coverImage,
        String created_at,
        String updated_at
) {
}
