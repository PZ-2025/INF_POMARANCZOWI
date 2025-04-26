package com.orange.bookmanagment.shared.events;

public record BookStatusChangedEvent(Long bookId, BookStatus newStatus) {}

