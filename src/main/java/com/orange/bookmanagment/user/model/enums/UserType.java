package com.orange.bookmanagment.user.model.enums;

import lombok.Getter;

import java.util.List;

/**
 * Enum reprezentujący typ użytkownika w systemie.
 *
 * <p>Każdy typ użytkownika zawiera listę ról (authorities), które są przypisane do danej roli.</p>
 * <ul>
 *     <li>{@code READER} – Zwykły użytkownik z rolami: USER, READER</li>
 *     <li>{@code LIBRARIAN} – Bibliotekarz z rolami: USER, LIBRARIAN</li>
 *     <li>{@code ADMIN} – Administrator z rolami: USER, ADMIN, LIBRARIAN</li>
 * </ul>
 */
@Getter
public enum UserType {
    READER(List.of("USER", "READER")),
    LIBRARIAN(List.of("USER", "LIBRARIAN")),
    ADMIN(List.of("USER", "ADMIN", "LIBRARIAN"));

    private final List<String> authorities;

    /**
     * Konstruktor przypisujący role do danego typu użytkownika.
     *
     * @param authorities lista ról przypisana do danego typu użytkownika
     */
    UserType(List<String> authorities) {
        this.authorities = authorities;
    }
}
