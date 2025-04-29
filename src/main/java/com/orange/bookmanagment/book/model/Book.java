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
@Setter
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
     * <p>Constructor for Book entity</p>
     *
     * @param title      title of book
     * @param authors    list of authors of book
     * @param publisher  publisher of book
     * @param description description of book
     * @param genre      genre of book
     * @param status     status of book
     * @param coverImage cover image of book
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
     * <p>Sets the status of the book.</p>
     *
     * @param status new status of the book
     */
    public void setStatus(BookStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
