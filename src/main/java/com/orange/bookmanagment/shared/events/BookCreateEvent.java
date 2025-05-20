package com.orange.bookmanagment.shared.events;

import java.util.List;

public record BookCreateEvent(
        String title,
        List<EventBookAuthor> authors,
        EventBookPublisher publisher,
        String description,
        String genre,
        String coverImage
) {
    public record EventBookAuthor(
            String firstName,
            String lastName,
            String biography
    ) { }

    public record EventBookPublisher(
            String name,
            String description
    ) { }
}
