package com.orange.bookmanagment.book.repository;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.shared.enums.BookStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repozytorium do zarządzania encjami {@link Book}.
 * <p>
 * Umożliwia operacje CRUD oraz rozszerzone zapytania przy użyciu Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class BookRepository {

    private final BookJpaRepository bookJpaRepository;

    /**
     * Zapisuje książkę w bazie danych.
     *
     * @param book książka do zapisania
     * @return zapisana encja książki
     */
    public Book saveBook(Book book) {
        return bookJpaRepository.save(book);
    }

    /**
     * Wyszukuje książkę po ID.
     *
     * @param id identyfikator książki
     * @return opcjonalna książka
     */
    public Optional<Book> findBookById(long id){
        return bookJpaRepository.findById(id);
    }

    /**
     * Pobiera wszystkie książki z paginacją.
     *
     * @param pageable dane o stronie i rozmiarze
     * @return strona książek
     */
    public Page<Book> findAllBooks(Pageable pageable){
        return bookJpaRepository.findAll(pageable);
    }

    /**
     * Wyszukuje książki po tytule.
     *
     * @param title tytuł książki
     * @return lista książek o podanym tytule
     */
    public List<Book> findBookByTitle(String title){
        return bookJpaRepository.findBookByTitle(title);
    }

    /**
     * Sprawdza, czy książka istnieje na podstawie ID.
     *
     * @param id identyfikator książki
     * @return true, jeśli istnieje; false w przeciwnym razie
     */
    public boolean existsById(long id) {
        return bookJpaRepository.existsById(id);
    }

    /**
     * Zwraca wszystkie książki (bez paginacji).
     *
     * @return lista wszystkich książek
     */
    public List<Book> findAll() {
        return bookJpaRepository.findAll();
    }

    /**
     * Zwraca książki o podanym statusie.
     *
     * @param status status książki
     * @return lista książek o wskazanym statusie
     */
    public List<Book> findByStatus(BookStatus status) {
        return bookJpaRepository.findByStatus(status);
    }

    /**
     * Zwraca losowe książki.
     *
     * @param limit maksymalna liczba książek do zwrócenia
     * @return lista losowych książek
     */
    public List<Book> findRandomBooks(int limit) {
        return bookJpaRepository.findRandomBooks(limit);
    }

    /**
     * Zwraca losowe książki dla podanego gatunku.
     *
     * @param genre gatunek książek
     * @param limit maksymalna liczba książek do zwrócenia
     * @return lista losowych książek z danego gatunku
     */
    public List<Book> findRandomBooksByGenre(String genre, int limit) {
        return bookJpaRepository.findRandomBooksByGenre(genre, limit);
    }

    /**
     * Zwraca 5 najpopularniejszych gatunków książek.
     *
     * @return lista nazw najpopularniejszych gatunków
     */
    public List<String> findTop5Genres() {
        return bookJpaRepository.findTop5Genres().stream()
                .map(row -> (String) row[0])
                .toList();
    }

    /**
     * Wyszukuje książki na podstawie zapytania tekstowego.
     *
     * @param query ciąg tekstowy do wyszukania (tytuł, autor, gatunek itp.)
     * @return lista książek pasujących do zapytania
     */
    public List<Book> searchBooks(String query) {
        return bookJpaRepository.searchBooks(query);
    }
}
