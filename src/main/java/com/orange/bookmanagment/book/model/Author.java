package com.orange.bookmanagment.book.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>Entity representing a book author.</p>
 *
 * <p>This class contains information about the author, such as first name, last name, and biography.</p>
 */
@Entity
@Table(name = "authors")
@Getter
//@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String firstName;
    private String lastName;
    @Column(length = 2000)
    private String biography;

    /**
     * <p>Constructor for Author entity</p>
     *
     * @param firstName firstname of author
     * @param lastName  lastname of author
     * @param biography biography of author
     */
    public Author(String firstName, String lastName, String biography) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.biography = biography;
    }
}
