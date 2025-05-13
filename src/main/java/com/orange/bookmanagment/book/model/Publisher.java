package com.orange.bookmanagment.book.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Encja reprezentująca wydawcę książki.
 * <p>
 * Zawiera nazwę wydawcy oraz jego opis.
 */
@Entity
@Table(name = "publishers")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    /**
     * Konstruktor tworzący wydawcę.
     *
     * @param name nazwa wydawcy
     * @param description opis wydawcy
     */
    public Publisher(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
