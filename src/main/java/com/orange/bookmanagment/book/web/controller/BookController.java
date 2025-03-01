package com.orange.bookmanagment.book.web.controller;

import com.orange.bookmanagment.book.service.BookService;
import com.orange.bookmanagment.book.web.mapper.BookDtoMapper;
import com.orange.bookmanagment.book.web.request.BookCreateRequest;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor

class BookController {
    private final BookService bookService;
    private final BookDtoMapper bookDtoMapper;

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getBookById(@PathVariable("id") long id) {
        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("Book data by id request")
                        .message("Book by id")
                        .data(Map.of("book", bookDtoMapper.toDto(bookService.getBookById(id))))
                        .build());
    }

    @GetMapping("/all")
    public ResponseEntity<HttpResponse> getAllBooks() {
        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("All books request")
                        .message("All books")
                        .data(Map.of("books", bookDtoMapper.toDtoList(bookService.getAllBooks())))
                        .build());
    }

    @PostMapping("/create")
    public ResponseEntity<HttpResponse> createBook(@RequestBody BookCreateRequest bookCreateRequest) {
        bookService.createBook(bookCreateRequest);
        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("Book creation request")
                        .message("Book created")
                        .build());
    }
}
