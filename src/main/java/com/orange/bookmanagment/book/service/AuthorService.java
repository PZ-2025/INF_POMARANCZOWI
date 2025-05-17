package com.orange.bookmanagment.book.service;

import com.orange.bookmanagment.book.model.Author;
import com.orange.bookmanagment.book.web.requests.AuthorCreateRequest;

import java.util.List;

/**
 * Serwis do zarządzania autorami książek.
 * <p>
 * Definiuje operacje związane z tworzeniem autorów.
 */
public interface AuthorService {

    /**
     * Tworzy listę autorów na podstawie przesłanych danych wejściowych.
     *
     * @param authorCreateRequests lista danych do utworzenia autorów
     * @return lista utworzonych encji autorów
     */
    List<Author> createAuthors(List<AuthorCreateRequest> authorCreateRequests);

    /**
     * Zwraca listę wszystkich autorów zapisanych w bazie danych.
     *
     * @return lista autorów
     */
    List<Author> getAllAuthors();
}
