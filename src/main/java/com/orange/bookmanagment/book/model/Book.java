package com.orange.bookmanagment.book.model;

import com.orange.bookmanagment.book.model.enums.BookStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

/**
 * <p>Book entity represent book information in system</p>
 *
 * <p>Book entity has many to many relationship with Author entity</p>
 * <p>BookStatus is represent as status of book in system</p>
 */

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;
    private String publisher;
    private String description;
    private String genre;
    @Enumerated(EnumType.STRING)
    private BookStatus status;
    private String coverImage;
    private Instant created_at;
    private Instant updated_at;

    /**
     * <p>Constructor for Book entity</p>
     *
     * @param title      title of book
     * @param authors    list of authors of book
     * @param publisher  publisher of book
     * @param description description of book
     * @param genre      genre of book
     * @param status     status of book
     * @param coverImage cover image of book
     * @param created_at created date of book
     * @param updated_at updated date of book
     */
    public Book(String title, List<Author> authors, String publisher, String description, String genre, BookStatus status, String coverImage, Instant created_at, Instant updated_at) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.description = description;
        this.genre = genre;
        this.status = status;
        this.coverImage = coverImage;
        this.created_at = Instant.now();
        this.updated_at = Instant.now();
    }
}
