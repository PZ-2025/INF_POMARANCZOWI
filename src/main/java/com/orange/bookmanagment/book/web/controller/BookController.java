package com.orange.bookmanagment.book.web.controller;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.service.BookService;
import com.orange.bookmanagment.book.web.mapper.BookDtoMapper;
import com.orange.bookmanagment.book.web.model.BookDto;
import com.orange.bookmanagment.book.web.requests.BookCreateRequest;
import com.orange.bookmanagment.shared.model.HttpResponse;
import com.orange.bookmanagment.shared.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

/**
 * BookController is a REST controller that handles requests related to books.
 * It provides endpoints for retrieving book information, creating new books, and managing book data.
 */
@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
class BookController {
    private final BookService bookService;
    private final BookDtoMapper bookDtoMapper;

    /**
     * Retrieves a book by its ID.
     *
     * @param id the ID of the book to retrieve
     * @return a ResponseEntity containing the book data
     */
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

    /**
     * Retrieves all books with pagination and sorting options.
     *
     * @param page          the page number to retrieve (default is 0)
     * @param size          the number of items per page (default is 10)
     * @param sortBy        the field to sort by (default is "id")
     * @param sortDirection the direction of sorting (default is "asc")
     * @return a ResponseEntity containing the paginated list of books
     */
    @GetMapping("/all")
    public ResponseEntity<HttpResponse> getAllBooks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );

        Page<Book> booksPage = bookService.getAllBooks(pageable);

        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("All books request")
                        .message("All books")
                        .data(Map.of("books", booksPage.stream().map(bookDtoMapper::toDto).toList(),
                                "currentPage", booksPage.getNumber(),
                                "totalPages", booksPage.getTotalPages(),
                                "totalItems", booksPage.getTotalElements(),
                                "pageSize", booksPage.getSize()
                                ))
                        .build());
    }

    /**
     * Creates a new book.
     *
     * @param bookCreateRequest the request object containing book creation data
     * @return a ResponseEntity containing the created book data
     */
    @PostMapping("/create")
    public ResponseEntity<HttpResponse> createBook(@RequestBody BookCreateRequest bookCreateRequest) {
        Book book = bookService.createBook(bookCreateRequest);
        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("Book creation request")
                        .message("Book created")
                        .data(Map.of("book", bookDtoMapper.toDto(book)))
                        .build());
    }

    @GetMapping("/random")
    public ResponseEntity<HttpResponse> getRandomBooks() {
        List<BookDto> books = bookService.getRandomBooks(12);
        return ResponseEntity.status(HttpStatus.OK).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .reason("Random books")
                        .message("Successfully fetched 12 random books")
                        .data(Map.of("books", books))
                        .build()
        );
    }

    @GetMapping("/random/category/{genre}")
    public ResponseEntity<HttpResponse> getRandomBooksByGenre(@PathVariable("genre") String genre) {
        List<BookDto> books = bookService.getRandomBooksByGenre(genre, 6);
        return ResponseEntity.status(HttpStatus.OK).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .reason("Random books by genre")
                        .message("Returned 6 random books from genre: " + genre)
                        .data(Map.of("books", books))
                        .build()
        );
    }

    @GetMapping("/top-genres")
    public ResponseEntity<HttpResponse> getTop5Genres() {
        List<String> genres = bookService.getTop5Genres();
        return ResponseEntity.status(HttpStatus.OK).body(
                HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .reason("Top genres request")
                        .message("Top 5 book genres")
                        .data(Map.of("genres", genres))
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<HttpResponse> searchBooks(@RequestParam("query") String query) {
        List<Book> books = bookService.searchBooks(query);
        return ResponseEntity.status(OK)
                .body(HttpResponse.builder()
                        .timeStamp(TimeUtil.getCurrentTimeWithFormat())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .reason("Book search request")
                        .message("Books matching query")
                        .data(Map.of("books", books.stream().map(bookDtoMapper::toDto).toList()))
                        .build());
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(Authentication authentication) {
        final Jwt jwt = (Jwt) authentication.getPrincipal();

        final long userId = jwt.getClaim("user_id");

        return ResponseEntity.status(OK).body("test");
    }
}
