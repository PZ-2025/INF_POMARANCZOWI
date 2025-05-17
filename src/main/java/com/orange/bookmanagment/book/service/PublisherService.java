package com.orange.bookmanagment.book.service;

import com.orange.bookmanagment.book.model.Publisher;
import com.orange.bookmanagment.book.web.requests.PublisherCreateRequest;

import java.util.List;

/**
 * Serwis do zarządzania wydawcami książek.
 * <p>
 * Definiuje operacje związane z tworzeniem wydawców.
 */
public interface PublisherService {

    /**
     * Tworzy nowego wydawcę na podstawie danych z żądania.
     *
     * @param publisherCreateRequest dane nowego wydawcy
     * @return utworzony wydawca
     */
    Publisher createPublisher(PublisherCreateRequest publisherCreateRequest);

    /**
     * Zwraca listę wszystkich wydawców zapisanych w bazie danych.
     *
     * @return lista wydawców
     */
    List<Publisher> getAllPublishers();
}
