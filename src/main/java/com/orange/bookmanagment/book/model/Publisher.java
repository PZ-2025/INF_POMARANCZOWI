package com.orange.bookmanagment.book.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "publishers")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String description;

    /**
     * <p>Constructor for Publisher entity</p>
     *
     * @param name        name of publisher
     * @param description description of publisher
     */
    public Publisher(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
