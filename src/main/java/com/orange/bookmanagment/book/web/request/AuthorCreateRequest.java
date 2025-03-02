package com.orange.bookmanagment.book.web.request;

public record AuthorCreateRequest(
        String firstName,
        String lastName,
        String biography
) {
}
