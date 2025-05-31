package com.orange.bookmanagment.book.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Encja reprezentująca autora książki.
 * <p>
 * Zawiera podstawowe dane autora: imię, nazwisko oraz biografię.
 */
@Entity
@Table(name = "authors")
@Getter
//@Setter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(length = 2000)
    private String biography;

    /**
     * Konstruktor tworzący autora
     *
     * @param firstName imię autora
     * @param lastName nazwisko autora
     * @param biography biografia autora
     */
    public Author(String firstName, String lastName, String biography) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.biography = biography;
    }
}
