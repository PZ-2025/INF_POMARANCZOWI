package com.orange.bookmanagment.book.listener;


import com.orange.bookmanagment.book.exception.BookStatusNotFoundException;
import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.enums.BookStatus;
import com.orange.bookmanagment.book.service.BookService;
import com.orange.bookmanagment.shared.events.LoanBookEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookListener {

    private final BookService bookService;


    @EventListener(LoanBookEvent.class)
    public void loanBookEvent(LoanBookEvent event){
        final Book book = bookService.getBookById(event.bookId());
        if(!BookStatus.existsByName(event.bookStatus().name())){
            throw new BookStatusNotFoundException(event.bookStatus() + " has not been found");
        }
        book.setStatus(BookStatus.valueOf(event.bookStatus().name()));


        switch (event.bookStatus()){
            //Tu możesz robic jakies wieksze rzeczy jak są potrzebne np nie wiem przy czymś robić coś tam wiesz oco chodzi
        }
    }

}
