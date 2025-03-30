package com.orange.bookmanagment.book.web.requests;

public record AuthorCreateRequest(
        String firstName,
        String lastName,
        String biography
) {
}
