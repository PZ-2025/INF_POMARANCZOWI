package com.orange.bookmanagment.order.model;

import com.orange.bookmanagment.book.web.requests.AuthorCreateRequest;
import com.orange.bookmanagment.book.web.requests.PublisherCreateRequest;

import java.util.List;

public record OrderedBook(
        String title,
        List<OrderedBookAuthor> authors,
        OrderedBookPublisher publisher,
        String description,
        String genre,
        String coverImage
) {

    public record OrderedBookAuthor(
            String firstName,
            String lastName,
            String biography
    ){

    }
    public record OrderedBookPublisher(
            String name,
            String description
    ){

    }


}
