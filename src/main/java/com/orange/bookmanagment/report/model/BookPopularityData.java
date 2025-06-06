package com.orange.bookmanagment.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model reprezentujący dane o popularności książki
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookPopularityData {
    private Long bookId;
    private String title;
    private String authors;
    private String publisher;
    private String genre;
    private String status;
    private int loanCount;  // liczba wypożyczeń
}