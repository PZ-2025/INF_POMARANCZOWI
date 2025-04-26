package com.orange.bookmanagment.book.listener;


import com.orange.bookmanagment.book.exception.BookNotAvailableException;
import com.orange.bookmanagment.book.exception.BookStatusNotFoundException;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.enums.BookStatus;
import com.orange.bookmanagment.book.service.BookService;
import com.orange.bookmanagment.shared.events.LoanBookEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookListener {

    private final BookService bookService;
    private final ApplicationEventPublisher eventPublisher;


    @EventListener(LoanBookEvent.class)
    public void loanBookEvent(LoanBookEvent event){
        final Book book = bookService.getBookById(event.bookId());
        if(event.bookStatus().equals(LoanBookEvent.BookStatus.BORROWED)) {
            if (book.getStatus() != BookStatus.AVAILABLE && book.getStatus() != BookStatus.RESERVED) {
                throw new BookNotAvailableException("Book is not available");
            }
        }
        //if book jest reserved =
        //tu juz ksiazka istnieje
        if(!BookStatus.existsByName(event.bookStatus().name())){
            throw new BookStatusNotFoundException(event.bookStatus() + " has not been found");
        }
        book.setStatus(BookStatus.valueOf(event.bookStatus().name()));

    }
}
