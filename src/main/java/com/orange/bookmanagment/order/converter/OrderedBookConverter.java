package com.orange.bookmanagment.order.converter;

import com.orange.bookmanagment.order.model.OrderedBook;
import com.orange.bookmanagment.order.util.GsonUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Konwerter atrybutów JPA do konwersji między listą obiektów OrderedBook a reprezentacją String
 * do przechowywania w bazie danych.
 *
 * <p>Ten konwerter serializuje listę obiektów OrderedBook do stringa JSON oddzielonego przecinkami
 * podczas zapisywania do bazy danych, oraz deserializuje string z powrotem do listy obiektów OrderedBook
 * podczas odczytywania z bazy danych.</p>
 *
 * <p>Proces konwersji wykorzystuje serializację JSON poprzez GsonUtil dla pojedynczych obiektów OrderedBook,
 * z separacją przecinkami dla wielu obiektów w liście.</p>
 *
 */
@Converter
public class OrderedBookConverter implements AttributeConverter<List<OrderedBook>, String> {

    /**
     * Konwertuje listę obiektów OrderedBook na reprezentację kolumny bazy danych.
     *
     * <p>Ta metoda serializuje każdy obiekt OrderedBook z listy do jego reprezentacji JSON string
     * używając metody toString(), następnie łączy wszystkie stringi JSON separatorami przecinków.</p>
     *
     * <p>Przykład wyniku: {@code {"id":1,"title":"Książka1"},{"id":2,"title":"Książka2"}}</p>
     *
     * @param attribute lista obiektów OrderedBook do konwersji; nie może być null
     * @return string oddzielony przecinkami z reprezentacjami JSON obiektów OrderedBook
     * @throws NullPointerException jeśli parametr attribute jest null
     * @throws IndexOutOfBoundsException jeśli wystąpią problemy z dostępem do elementów listy
     */
    @Override
    public String convertToDatabaseColumn(List<OrderedBook> attribute) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < attribute.size(); i++) {
            sb.append(attribute.get(i).toString());
            if(i != attribute.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    /**
     * Konwertuje dane z bazy danych (string) na listę obiektów OrderedBook.
     *
     * <p>Ta metoda dzieli string wejściowy na podstawie separatora przecinka, następnie
     * deserializuje każdy fragment JSON do obiektu OrderedBook używając GsonUtil.</p>
     *
     * <p>Przykład danych wejściowych: {@code {"id":1,"title":"Książka1"},{"id":2,"title":"Książka2"}}</p>
     *
     * @param dbData string z bazy danych zawierający reprezentacje JSON oddzielone przecinkami; nie może być null
     * @return lista obiektów OrderedBook zdeserializowanych z danych wejściowych
     * @throws NullPointerException jeśli parametr dbData jest null
     * @throws com.google.gson.JsonSyntaxException jeśli dane JSON są nieprawidłowe podczas deserializacji
     * @throws IllegalArgumentException jeśli format danych jest nieprawidłowy
     */
    @Override
    public List<OrderedBook> convertToEntityAttribute(String dbData) {
        String[] data = dbData.split(",");

        final List<OrderedBook> books = new ArrayList<>();

        for (String s : data) {
            final OrderedBook orderedBook = GsonUtil.fromJson(s, OrderedBook.class);
            books.add(orderedBook);
        }

        return books;
    }
}