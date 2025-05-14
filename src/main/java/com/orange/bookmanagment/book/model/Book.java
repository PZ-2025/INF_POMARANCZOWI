package com.orange.bookmanagment.book.model;

import com.orange.bookmanagment.shared.enums.BookStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

/**
 * Encja reprezentująca książkę w systemie.
 * <p>
 * Zawiera dane takie jak tytuł, autorzy, wydawca, opis, gatunek, status czy okładka.
 * <p>
 * Książka posiada relację wiele-do-wielu z autorami oraz wiele-do-jednego z wydawcą.
 */
@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    private String description;

    private String genre;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    private String coverImage;

    private Instant createdAt;

    private Instant updatedAt;

    /**
     * Konstruktor tworzący książkę.
     * Daty są ustawiane automatycznie.
     *
     * @param title tytuł książki
     * @param authors lista autorów
     * @param publisher wydawca książki
     * @param description opis książki
     * @param genre gatunek książki
     * @param status status książki
     * @param coverImage adres URL do okładki
     */
    public Book(String title, List<Author> authors, Publisher publisher, String description, String genre, BookStatus status, String coverImage) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.description = description;
        this.genre = genre;
        this.status = status;
        this.coverImage = coverImage;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Ustawia nowy status książki i aktualizuje datę modyfikacji.
     *
     * @param status nowy status książki
     */
    public void setStatus(BookStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
