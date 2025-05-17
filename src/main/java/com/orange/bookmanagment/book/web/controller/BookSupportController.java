package com.orange.bookmanagment.book.web.controller;

import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.book.repository.AuthorRepository;
import com.orange.bookmanagment.book.repository.PublisherRepository;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookSupportController {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    /**
     * Zwraca wszystkich autorów.
     *
     * @return odpowiedź HTTP z listą autorów
     */
    @GetMapping("/authors")
    public ResponseEntity<HttpResponse> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();

        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("All authors request")
                        .message("Successfully fetched all authors")
                        .data(Map.of("authors", authors))
                        .build());
    }

    /**
     * Zwraca wszystkich wydawców.
     *
     * @return odpowiedź HTTP z listą wydawców
     */
    @GetMapping("/publishers")
    public ResponseEntity<HttpResponse> getAllPublishers() {
        List<Publisher> publishers = publisherRepository.findAll();

        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("All publishers request")
                        .message("Successfully fetched all publishers")
                        .data(Map.of("publishers", publishers))
                        .build());
    }
}
