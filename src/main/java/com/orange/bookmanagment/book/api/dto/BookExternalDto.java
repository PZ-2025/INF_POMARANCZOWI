package com.orange.bookmanagment.book.api.dto;

import com.orange.bookmanagment.shared.enums.BookStatus;

import java.time.Instant;
import java.util.List;

public record BookExternalDto(
        Long id,
        String title,
        List<AuthorExternalDto> authors,
        PublisherExternalDto publisher,
        String description,
        String genre,
        BookStatus status,
        String coverImage,
        Instant createdAt,
        Instant updatedAt
) { }
