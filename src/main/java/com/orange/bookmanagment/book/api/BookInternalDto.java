package com.orange.bookmanagment.book.api;

import com.orange.bookmanagment.book.api.dto.AuthorInternalDto;
import com.orange.bookmanagment.book.api.dto.PublisherInternalDto;
import com.orange.bookmanagment.shared.enums.BookStatus;

import java.time.Instant;
import java.util.List;

public record BookInternalDto(
        Long id,
        String title,
        List<AuthorInternalDto> authors,
        PublisherInternalDto publisher,
        String description,
        String genre,
        BookStatus status,
        String coverImage,
        Instant createdAt,
        Instant updatedAt
) {}
