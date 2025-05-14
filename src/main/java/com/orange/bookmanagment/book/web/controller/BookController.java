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
 * Kontroler REST obsługujący operacje na książkach.
 * Umożliwia pobieranie książek, tworzenie nowych, wyszukiwanie oraz inne operacje pomocnicze.
 */
@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
class BookController {

    private final BookService bookService;
    private final BookDtoMapper bookDtoMapper;

    /**
     * Zwraca książkę o podanym ID.
     *
     * @param id identyfikator książki
     * @return odpowiedź z danymi książki
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
     * Zwraca wszystkie książki w formie stronicowanej i posortowanej.
     *
     * @param page numer strony (domyślnie 0)
     * @param size rozmiar strony (domyślnie 10)
     * @param sortBy pole do sortowania (domyślnie "id")
     * @param sortDirection kierunek sortowania: asc lub desc (domyślnie asc)
     * @return odpowiedź z listą książek i informacją o stronie
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
     * Tworzy nową książkę.
     *
     * @param bookCreateRequest dane nowej książki
     * @return odpowiedź z utworzoną książką
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

    /**
     * Zwraca losowe książki.
     *
     * @return lista 12 losowych książek
     */
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

    /**
     * Zwraca losowe książki dla danego gatunku.
     *
     * @param genre gatunek książki
     * @return lista 6 losowych książek z danego gatunku
     */
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

    /**
     * Zwraca 5 najpopularniejszych gatunków książek.
     *
     * @return lista nazw gatunków
     */
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

    /**
     * Wyszukuje książki na podstawie zapytania tekstowego.
     *
     * @param query fraza wyszukiwania
     * @return lista dopasowanych książek
     */
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

    /**
     * Endpoint testowy do autoryzacji.
     *
     * @param authentication obiekt uwierzytelniający
     * @return wiadomość testowa
     */
    @GetMapping("/test")
    public ResponseEntity<String> test(Authentication authentication) {
        final Jwt jwt = (Jwt) authentication.getPrincipal();

        final long userId = jwt.getClaim("user_id");

        return ResponseEntity.status(OK).body("test");
    }
}
