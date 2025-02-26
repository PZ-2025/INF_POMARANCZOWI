package com.orange.bookmanagment.user.model.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum UserType {

    READER(List.of("USER","READER")),
    LIBRARIAN(List.of("USER","LIBRARIAN")),
    ADMIN(List.of("USER","ADMIN", "LIBRARIAN"));

    private final List<String> authorities;

    UserType(List<String> authorities) {
        this.authorities = authorities;
    }

}
