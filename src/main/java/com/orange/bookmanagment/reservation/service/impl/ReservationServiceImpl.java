package com.orange.bookmanagment.reservation.service.impl;

import com.orange.bookmanagment.book.model.Book;
import com.orange.bookmanagment.book.model.enums.BookStatus;
import com.orange.bookmanagment.book.repository.BookRepository;
import com.orange.bookmanagment.reservation.exception.BookAlreadyReservedException;
import com.orange.bookmanagment.reservation.model.Reservation;
import com.orange.bookmanagment.reservation.model.enums.ReservationStatus;
import com.orange.bookmanagment.reservation.repository.ReservationRepository;
import com.orange.bookmanagment.reservation.service.ReservationService;
import com.orange.bookmanagment.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ReservationServiceImpl is an implementation of the ReservationService interface.
 * It provides methods to create reservations and check the status of reservations.
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
//    private final LoanService loanService;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Reservation createReservation(Book book, User user) {

        // Sprawdz czy książka nie jest już wypożyczona przez tego użytkownika
//        if (loanService.isBookBorrowed(book) && loanService.getActiveUserLoans(user).stream()
//                .anyMatch(loan -> loan.getBook().equals(book))) {
//            throw new IllegalStateException("You already have this book borrowed");
//        }

        // Sprawdz czy użytkownik nie ma już aktywnej rezerwacji na tę książkę
        if (isBookReservedForUser(book, user)) {
            throw new BookAlreadyReservedException("You already have an active reservation for this book");
        }

        // Sprawdz czy użytkownik nie przekroczył limitu rezerwacji
//        List<Reservation> activeReservations = getActiveReservations(user);
//        if (activeReservations.size() >= maxReservationsPerUser) {
//            throw new IllegalStateException("You have reached the maximum number of active reservations ("
//                    + maxReservationsPerUser + ")");
//        }

        final int queuePosition = countActiveReservations(book) + 1;

        Reservation reservation = new Reservation(
                book,
                user,
                ReservationStatus.PENDING,
                queuePosition
        );

        if (book.getStatus() == BookStatus.AVAILABLE) {
            reservation.setStatus(ReservationStatus.READY);

            book.setStatus(BookStatus.RESERVED);
            bookRepository.saveBook(book);
        }

        return reservationRepository.saveReservation(reservation);
    }

    /**
     * Checks if a book is reserved for a specific user.
     *
     * @param book the book to check
     * @param user the user to check for
     * @return true if the book is reserved for the user, false otherwise
     */
    public boolean isBookReservedForUser(Book book, User user) {
        return reservationRepository.existsByBookAndUserAndStatusIn(
                book, user, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }

    /**
     * Counts the number of active reservations for a given book.
     *
     * @param book the book to check for active reservations
     * @return the number of active reservations for the book
     */
    public int countActiveReservations(Book book) {
        return reservationRepository.countByBookAndStatusIn(
                book, List.of(ReservationStatus.PENDING, ReservationStatus.READY));
    }
}
