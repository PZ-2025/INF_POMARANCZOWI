package com.orange.bookmanagment.book.handler;


import com.orange.bookmanagment.book.service.BookService;
import com.orange.bookmanagment.book.web.requests.AuthorCreateRequest;
import com.orange.bookmanagment.book.web.requests.BookCreateRequest;
import com.orange.bookmanagment.book.web.requests.PublisherCreateRequest;
import com.orange.bookmanagment.shared.events.BookCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BookEventHandler {


    private final BookService bookService;


    @EventListener(BookCreateEvent.class)
    public void onBookCreate(BookCreateEvent event) {

        bookService.createBook(new BookCreateRequest(event.title(),
                event.authors().stream().map(eventBookAuthor -> new AuthorCreateRequest(eventBookAuthor.firstName(), eventBookAuthor.lastName(), eventBookAuthor.biography())).toList(),
                new PublisherCreateRequest(event.publisher().name(),event.publisher().description()),
                event.description(),
                event.genre(),
                event.coverImage()));

    }

}
