package com.orange.bookmanagment.book.api.dto;

public record AuthorExternalDto(
        Long id,
        String firstName,
        String lastName
) { }
